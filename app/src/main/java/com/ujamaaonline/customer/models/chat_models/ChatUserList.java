package com.ujamaaonline.customer.models.chat_models;

public class ChatUserList {
    private String businessName;
    private String businessId;
    private String businessLogo;
    private String message;
    private String senderType;
    private String timeStamp;
    private String archive;
    private String blockByBusiness;
    private String blockByCustomer;
    private String block;
    private String flag;
    private String leaveByBusiness;
    private String leaveByCustomer;
    private String markUnread;
    private String messageType;
    private String messagingStatus;

    public ChatUserList(String businessName, String businessId, String businessLogo, String message, String senderType, String timeStamp, String archive, String blockByBusiness, String blockByCustomer, String block, String flag, String leaveByBusiness, String leaveByCustomer, String markUnread, String messageType,String messagingStatus) {
        this.businessName = businessName;
        this.businessId = businessId;
        this.businessLogo = businessLogo;
        this.message = message;
        this.senderType = senderType;
        this.timeStamp = timeStamp;
        this.archive = archive;
        this.blockByBusiness = blockByBusiness;
        this.blockByCustomer = blockByCustomer;
        this.block = block;
        this.flag = flag;
        this.leaveByBusiness = leaveByBusiness;
        this.leaveByCustomer = leaveByCustomer;
        this.markUnread = markUnread;
        this.messageType = messageType;
        this.messagingStatus=messagingStatus;
    }
    public ChatUserList( String businessId, String archive ) {
        this.businessId = businessId;
        this.archive = archive;
        this.blockByBusiness = blockByBusiness;
        this.blockByCustomer = blockByCustomer;
        this.block = block;
        this.flag = flag;
        this.leaveByBusiness = leaveByBusiness;
        this.leaveByCustomer = leaveByCustomer;
        this.markUnread = markUnread;
        this.messageType = messageType;
        this.messagingStatus=messagingStatus;
    }
    public String getBusinessName() {
        return businessName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getArchive() {
        return archive;
    }

    public String getBlockByBusiness() {
        return blockByBusiness;
    }

    public String getBlockByCustomer() {
        return blockByCustomer;
    }

    public String getBlock() {
        return block;
    }

    public String getFlag() {
        return flag;
    }

    public String getLeaveByBusiness() {
        return leaveByBusiness;
    }

    public String getLeaveByCustomer() {
        return leaveByCustomer;
    }

    public String getMarkUnread() {
        return markUnread;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setMarkUnread(String markUnread) {
        this.markUnread = markUnread;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessagingStatus() {
        return messagingStatus;
    }
}
