const express = require('express');
const jwt = require('jsonwebtoken');
const bcryptjs = require('bcryptjs');
const responseModel = require('../models/ResponseModel');
const router = express.Router();

//Helpers
const {ResponseModel} = require('../models/ResponseModel');

// Schemas
const User = require('../schema/user');

// HTTP
router.post("/login",(req,res)=>{
    const {username,password} = req.body;
    User.findOne({username},(err,user)=>{
        if(err){
            const responseModel = new ResponseModel("Bad Credentials","Sunucu ile bağlantı kurulamadı!",500);
            res.json(responseModel);
        }
        if(user === null){
            const responseModel = new ResponseModel("Bad Credentials","Kullanıcı adı bulunamadı!",401);
            res.json(responseModel);
        }else{
            bcryptjs.compare(password,user.password)
            .then( result =>{
                if(!result){
                    const responseModel = new ResponseModel("Bad Credentials","Parola bulunamadı!",401);
                    res.json(responseModel);
                }else{
                    const payload = {
                        username
                    };
                    const token = jwt.sign(payload,req.app.get('api_secret_key'),{expiresIn : "1h"});
                    const responseModel = new ResponseModel("Success Login",token,200);
                    res.json(responseModel);
                }                    
            })
            .catch(error => {
                const responseModel = new ResponseModel("Bcrypt Error","Sunucu ile bağlantı kurulamadı!",500);
                res.json(responseModel);
            });
        }
    });
});

router.post("/register",(req,res)=>{
    const {username,password,email} = req.body;
    bcryptjs.hash(password,10,(error,hash)=>{
        if(error){
            const responseModel = new ResponseModel("Bcrypt Error","Sunucu ile bağlantı kurulamadı!",500);
            res.json(responseModel);
        }else{
            const user = new User({
                username,
                password:hash,
                email
            });
            user.save((error,result)=>{
                if(error){
                    const responseModel = new ResponseModel("Register Error","Kayıt sırasında bir hata oluştu.. Lütfen daha sonra tekrar deneyin!",401);
                    res.json(responseModel);
                }else{
                    const responseModel = new ResponseModel("Success Register","Kayıt Başarılı",200);
                    res.json(responseModel);
                }
            });
        }
    });
});

module.exports = router;