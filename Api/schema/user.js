const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({
    username:{
        type : String,
        required:true,
        unique:true
    },
    password:{
        type : String,
        required:true
    },
    email:{
        type : String,
        required:true,
        unique:true
    },
    firstname:{
        type:String,
        default:"isim"
    },
    lastname:{
        type:String,
        default:"soyisim"
    },
    photo:{
        type:String,
        default:"none"
    },
    job:{
        type:String,
        default:"işim yok"
    },
    gender:{
        type:String,
        default:"Belirtilmemiş"
    },
    biography:{
        type:String,
        default:"Hakkımda birşey bilmiyorsun :)"
    }
});

const User = mongoose.model("User",userSchema,"User");
module.exports = User;