package com.executivestrokes.mcent;

public class GroupModel {
    String groupID;
    String groupTitle;
    String groupDescription;
    String TotalMember;
    String tAmount;
    String pDuration;
    String startDate;

    String endDate;

    public GroupModel() {
    }

    public GroupModel(String groupID, String groupTitle, String groupDescription, String TotalMember, String tAmount,String pDuration,String startDate, String endDate) {
        this.groupID = groupID;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.TotalMember = TotalMember;
        this.tAmount = tAmount;
        this.pDuration=pDuration;
        this.startDate=startDate;
        this.endDate=endDate;

    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getTotalMember() {
        return TotalMember;
    }

    public void setTotalMember(String TotalMember) {
        this.TotalMember = TotalMember;
    }

    public String gettAmount() {
        return tAmount;
    }

    public void settAmount(String tAmount) {
        this.tAmount = tAmount;
    }

    public String getpDuration() {
        return pDuration;
    }

    public void setpDuration(String pDuration) {
        this.pDuration = pDuration;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
