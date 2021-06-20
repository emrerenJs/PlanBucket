const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const groupSchema = new Schema({
    title:{
        type:String,
        required:true,
        unique:true
    },
    members:[
        {
            username:{
                type:String,
                required:true
            },
            role:{
                type:String,
                default:"worker"
            }
        }
    ],
    messages:[
        {
            messageType:String,
            message:String,
            username:String,
            date:{
                type:Date,
                default:Date.now
            }
        }
    ],
    todos:[
        {
            todoTitle:String,
            todoBody:String,
            username:String,
            isFinished:Boolean
        }
    ]
});

const Group = mongoose.model("Group",groupSchema,"Group");
module.exports = Group;