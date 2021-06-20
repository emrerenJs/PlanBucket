const express = require('express');
const route = express.Router();
const fs = require('fs');
const mime = require('mime');

// Models
const {ResponseModel} = require("../models/ResponseModel");


// Schemas
const User = require("../schema/user");

// HTTP localhost:3000/profile/updateProfile
route.post("/getProfile",(req,res)=>{
    const {username} = req.body;
    User.findOne({username},"-_id photo job gender biography firstname lastname",(error,user)=>{
        if(error){
            const responseModel = new ResponseModel("Profile error","Sunucuya erişilemiyor!",500);
            res.json(responseModel);
        }else{
            const responseModel = new ResponseModel("Profile success",JSON.stringify(user),200);
            res.json(responseModel);
        }
    });
})

route.post('/updateProfile',(req,res)=>{
    const {username,photo,job,gender,biography,firstname,lastname} = req.body;
    if(photo === "none"){
        User.updateOne(
            {username},
            {
                job,
                gender,
                biography,
                firstname,
                lastname
            },
            (error,data)=>{
                if(error){
                    const responseModel = new ResponseModel("Update Error","Sunucu hatası!",500);
                    res.json(responseModel);
                }
                const responseModel = new ResponseModel("Update Success","Güncelleme başarılı!",200);
                res.json(responseModel);
            }
        );
    }else{
        path = `images/profile/${username}_profile_photo.jpeg`;
        fs.writeFile("./public/" + path,photo,"base64",(err)=>{
            User.updateOne(
                {username},
                {
                    photo:path,
                    job,
                    gender,
                    biography,
                    firstname,
                    lastname
                },
                (error,data)=>{
                    if(error){
                        const responseModel = new ResponseModel("Update Error","Sunucu hatası!",500);
                        res.json(responseModel);
                    }
                    const responseModel = new ResponseModel("Update Success","Güncelleme başarılı!",200);
                    res.json(responseModel);
                }
            );
        });
    }
});

module.exports = route;