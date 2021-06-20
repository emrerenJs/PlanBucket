const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const inviteSchema = new Schema({
    username:{
        type:String,
        required:true
    },
    groupTitle:{
        type:String,
        required:true
    }
},{autoCreate : true});

const Invite = mongoose.model("Invite",inviteSchema,"Invite");
module.exports = Invite;