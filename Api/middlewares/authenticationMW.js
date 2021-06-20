const jwt = require('jsonwebtoken');
const bcryptjs = require('bcryptjs');

//Models
const {ResponseModel} = require('../models/ResponseModel');

//HTTP
const verifyToken = (req,res,next) => {
    const token = req.headers["x-access-token"] || req.body.token || req.query.token;
    if(!token){
        const responseModel = new ResponseModel("error","No token provided!",401);
        res.json(responseModel);
    }else{
        jwt.verify(token,req.app.get('api_secret_key'),(error,decode) => {
            if(error){
                const responseModel = new ResponseModel("error","Oturum süreniz doldu!",401);
                res.json(responseModel);
            }else{
                req.decode = decode;
                next();
            }
        });   
    }
}

module.exports = {verifyToken}