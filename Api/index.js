'use strict'
const express = require('express');
const app = express();
const http = require("http").Server(app);
const enviroments = require('dotenv');
const bodyParser = require('body-parser');
const config = require('./config');
const mongoose = require("mongoose");
const io = require("socket.io")(http);

io.eio.pingTimeout = 120000;
io.eio.pingInterval = 5000;

//api configurations
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));
app.set('api_secret_key',config.api_secret_key);
app.use(express.static("public"));

//env variables
enviroments.config();

//database
mongoose.connect(process.env.CONNECTION_STRING, {useUnifiedTopology:true,useNewUrlParser:true})
    .then(()=>{
        console.log("Connected (Mongo DB)");
    })
    .catch((error)=>{
        console.log("Connection error (Mongo DB)");
        console.log(error);
    })

//models
const {ResponseModel} = require('./models/ResponseModel');
const {MessageModel} = require('./models/MessageModel');
//test
app.get("/",(req,res)=>{
    const responseModel = new ResponseModel("head","body",200);
    res.json(responseModel);
});

//middlewares
const {verifyToken} = require('./middlewares/authenticationMW');
//routes
const authentication = require('./routes/authentication');
const profile = require('./routes/profile');
const groupOps = require('./routes/groupOps');

/*Notification Socket*/
const userSockets = {};

const inviteNotificationIO = io.of("/inviteNotification");
inviteNotificationIO.on("connection", socket => {
    console.log("Someone connected to invite notification");
    socket.on("sendUsername",username => {
        userSockets[username] = socket;
    })
    socket.on("disconnect",()=>{
        console.log("Someone disconnected from invite notification");
    })
})


/*Notification Socket*/

const sockets = {
    userSockets
}

app.use("/auth",authentication);
app.use('/profile',verifyToken,profile);
app.use('/group',[(req,res,next)=>{req.sockets = sockets; next();},verifyToken],groupOps);


//socket
const Group = require('./schema/group');


const messagesio = io.of("/messages");

messagesio.on("connection", socket => {
    socket.on("joinChat", data =>{
        socket.username = data.username;
        socket.join(data.title,()=>{
            if(data.isNotificationClient){
                console.log(`${data.title} grubuna ${data.username} bağlandı (Notification)`);
            }else{
                const messageModel = JSON.stringify(new MessageModel(data.title,data.username,"information","bağlandı"));
                messagesio.to(data.title).emit("newJoin",messageModel);
                console.log(`${data.title} grubuna ${data.username} bağlandı (Realtime)`);
            }

        });
    });
    socket.on("sendMessage", data=>{
        const {messageType,username,message,title} = data;
        Group.findOneAndUpdate(
            {title},
            {
                $push : {
                    messages : {
                        messageType,
                        message,
                        username
                    }
                }
            },(err,res)=>{
                if(err)
                    console.log(err);
                const datetime = new Date();
                const messageModel = JSON.stringify(new MessageModel(data.title,data.username,"message",data.message,datetime));
                messagesio.to(data.title).emit("messageReceive",messageModel);
            }
        );
    });
    socket.on("disconnect",()=>{
        console.log("Disconnected " + socket.id);
    });
    socket.on("leaveRoom", data =>{
        socket.leave(data.title,()=>{
            if(data.isNotificationClient){
                console.log(`${data.title} grubundan ${data.username} ayrıldı (Notification)`);
            }else{
                const messageModel = JSON.stringify(new MessageModel(data.title,data.username,"information","çıktı"));
                messagesio.to(data.title).emit("leaved",messageModel);
                console.log(data.username + " odadan ayrıldı (Realtime)");
            }

        });
    });
});

app.get('/socket_test',(req,res)=>{
    res.sendFile(__dirname+"/socket_test.html");
});

//http port
http.listen(process.env.PORT,()=>{
    console.log(`${process.env.PORT} listening..`)
});