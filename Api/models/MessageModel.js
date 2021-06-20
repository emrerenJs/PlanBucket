class MessageModel{
    constructor(group,username,messageType,message,date){
        this.username = username;
        this.messageType = messageType;
        this.message = message;
        this.date = date;
        this.group = group;
    }
}

module.exports = {MessageModel};