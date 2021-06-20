const express = require('express');
const { NotificationInviteModel } = require('../models/NotificationInviteModel');
const router = express.Router();

//Models
const {ResponseModel} = require('../models/ResponseModel');

// Schemas
const Group = require('../schema/group');
const Invite = require('../schema/invites');
const User = require("../schema/user");

// HTTP
router.post("/myGroups",(req,res)=>{
    const {username} = req.body;
    Group.find({"members.username" : username},"-_id title",(error,groups)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucuya bağlanılamıyor!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Group success",JSON.stringify(groups),200);
            res.json(responseModel);
        }
    });
});

router.post("/addGroup",(req,res)=>{
    const {title,username} = req.body;
    const group = new Group({
        title,
        members : [{username,role:"admin"}]
    });
    group.save((error,result)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucuya bağlanılamıyor!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Group success",JSON.stringify(result),200);
            res.json(responseModel);
        }
    });
});

router.post("/getTodos",(req,res)=>{
    const {title} = req.body;
    Group.findOne({title},"-_id todos",(error,todos)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucu hatası",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Group success",JSON.stringify(todos.todos),200);
            res.json(responseModel);
        }
    });
});

router.post("/getMyTodos",(req,res)=>{
    const {title,username} = req.body;
    Group.findOne({title,"todos.username":username},"-_id todos",(error,mytodos)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucu hatası!",500);
            res.json(responseModel);
        }else{
            if(mytodos !== null && mytodos.todos){
                const filtered = mytodos.todos.filter((todo)=>{return todo.username == username});
                const responseModel = new ResponseModel("Group success",JSON.stringify(filtered),200);
                res.json(responseModel);
            }else{
                const responseModel = new ResponseModel("Group warning",JSON.stringify("Bos"),100);
                res.json(responseModel);
            }
        }
    })
});

router.post("/getGroupMembers",(req,res)=>{
    const {title} = req.body;
    Group.findOne({title},"-_id members",(error,members)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucu hatası!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Group success",JSON.stringify(members.members),200);
            res.json(responseModel);
        }
    });
});

router.post("/getMyGroupInfo",(req,res)=>{
    const {title,username} = req.body;
    Group.findOne({title,"members.username":username},(error,user)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucuyla bağlantı kurulamıyor!",500);
            res.json(responseModel);
        }else{
            for(let i = 0; i < user.members.length; i++){
                if(user.members[i].username === username){
                    const responseModel = new ResponseModel("Group success",JSON.stringify(user.members[i]),200);
                    res.json(responseModel);
                }
            }
        }
    });
});

router.post("/addTodo",(req,res)=>{
    const {title,username,todoTitle,todoBody} = req.body;
    Group.findOne({title,"todos.todoTitle":todoTitle},(error,group)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","sunucu hatası",500);
            res.json(responseModel);
        }else{
            if(group !== null){
                const responseModel = new ResponseModel("Group error","Bu Todo daha önce eklenmiş!",500);
                res.json(responseModel);       
            }else{
                Group.findOneAndUpdate(
                    {title},
                    {
                        $push : {
                            todos : {
                                todoTitle,
                                todoBody,
                                username,
                                isFinished:false
                            }
                        }
                    },
                    (error,result)=>{
                        if(error){
                            const responseModel = new ResponseModel("Group error","sunucu hatası",500);
                            res.json(responseModel);
                        }else{
                            const responseModel = new ResponseModel("Group success","Todo başarıyla eklendi",200);
                            res.json(responseModel);
                        }
                    }
                );
            }
        }
    });
});

router.post("/finishTodo",(req,res)=>{
    const {title,todoTitle,finishResult} = req.body;
    Group.findOneAndUpdate(
        {title, "todos.todoTitle":todoTitle},
        {
            $set : {
                "todos.$.isFinished" : finishResult
            }
        },
        (error,result)=>{
            if(error){
                const responseModel = new ResponseModel("Group error","Sunucu hatası!",500);
                res.json(responseModel);
            }else{
                const responseModel = new ResponseModel("Group success","ok",200);
                res.json(responseModel);
            }
        }
    );
});

router.post("/getMyInvites",(req,res)=>{
    const {username} = req.body;
    Invite.find({username},"-_id groupTitle",(error,invites)=>{
        if(error){
            const responseModel = new ResponseModel("Invite error","Sunucuya bağlanılamıyor!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Invite success",JSON.stringify(invites),200);
            res.json(responseModel);
        }
    });
});

router.post("/inviteUser",(req,res)=>{
    const {title,username} = req.body;
    User.findOne({username},(error,result)=>{
        if(result === null){
            const responseModel = new ResponseModel("Invite error","Kullanıcı bulunamadı",500);
            res.json(responseModel);
        }else{
            Invite.findOne({username,groupTitle:title},(error,invites)=>{
                if(error){
                    const responseModel = new ResponseModel("Invite error",JSON.stringify(error),500);
                    res.json(responseModel);
                }else{
                    if(invites !== null){
                        const responseModel = new ResponseModel("Invite error","Bu kişiye zaten davet gönderilmiş!",500);
                        res.json(responseModel);
                    }else{
                        Group.findOne({title,"members.username":username},(error,group)=>{
                            if(error){
                                const responseModel = new ResponseModel("Group error","Sunucuya bağlanılamıyor",500);
                                res.json(responseModel);
                            }else{
                                if(group !== null){
                                    const responseModel = new ResponseModel("Invite error","Bu kişi zaten grubunuzda!",500);
                                    res.json(responseModel);
                                }else{
                                    const invite = new Invite({
                                        username,
                                        groupTitle:title
                                    });
                                    invite.save((error,result)=>{
                                        if(error){
                                            const responseModel = new ResponseModel("Invite error","Sunucuya bağlanılamıyor",500);
                                            res.json(responseModel);
                                        }else{
                                            const responseModel = new ResponseModel("Invite success","Davet başarıyla gönderildi",200);
                                            req.sockets.userSockets[username].emit("onInviteNotification",JSON.stringify(new NotificationInviteModel(title,"Bir yeni davet!",title + " grubuna davet edildiniz..")));
                                            res.json(responseModel);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            })
        }
    });
});

router.post("/acceptInvite",(req,res)=>{
    const {title,username} = req.body;
    Invite.findOne({groupTitle:title,username},(error,invite)=>{
        if(error){
            const responseModel = new ResponseModel("Invite error","Sunucu hatası",500);
            res.json(responseModel);
        }else{
            invite.remove((error,result)=>{
                if(error){
                    const responseModel = new ResponseModel("Invite error","Sunucu hatası",500);
                    res.json(responseModel);
                }else{
                    Group.findOneAndUpdate(
                        {
                            title
                        },
                        {
                            $push : {
                                members : {
                                    username
                                }
                            }
                        },
                        (error,result)=>{
                            if(error){
                                const responseModel = new ResponseModel("Invite error","Sunucu hatası",500);
                                res.json(responseModel);
                            }else{
                                const responseModel = new ResponseModel("Invite success","Gruba katılım başarılı!",200);
                                res.json(responseModel);
                            }
                        }
                    );
                }
            });
        }
    });
});

router.post("/denyInvite",(req,res)=>{
    const {title,username} = req.body;
    Invite.findOne({groupTitle:title,username},(error,invite)=>{
        if(error){
            const responseModel = new ResponseModel("Invite error","Sunucu hatası",500);
            res.json(responseModel);
        }else{
            invite.remove((error,result)=>{
                if(error){
                    const responseModel = new ResponseModel("Invite error","Sunucu hatası",500);
                    res.json(responseModel);
                }else{
                    const responseModel = new ResponseModel("Invite success","Grup isteği başarıyla reddedildi",200);
                    res.json(responseModel);
                }
            });
        }
    });
});

router.post("/removeTodo",(req,res)=>{
    const {title,todoTitle} = req.body;
    
});

router.post("/removeUser",(req,res)=>{
    const {title,username} = req.body;
    const responseModel = new ResponseModel("Test",username + " removed", 200);
    Group.findOneAndUpdate({title},{$pull : {members : {username}}},(error,group)=>{
        if(error){
            const responseModel = new ResponseModel("Group error","Sunucuya bağlanılamıyor!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Group success",`${username} başarıyla silindi`,200);
            res.json(responseModel);
        }
    }); 
});

router.post("/getMessages",(req,res)=>{
    const {title} = req.body;
    Group.findOne(
        {title},
        (error,messages)=>{
            if(error){
                const responseModel = new ResponseModel("Group error","Sunucu hatası",500);
                res.json(responseModel);
            }else{
                const responseModel = new ResponseModel("Group success",JSON.stringify(messages.messages),200);
                res.json(responseModel);
            }
        });
});

module.exports = router;