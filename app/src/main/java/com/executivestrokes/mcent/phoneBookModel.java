package com.executivestrokes.mcent;
public class phoneBookModel {
    String participantsName,participantsNumber;

    public phoneBookModel() {
    }

    public phoneBookModel(String participantsName, String participantsNumber) {
        this.participantsName = participantsName;
        this.participantsNumber = participantsNumber;
    }

    public String getParticipantsName() {
        return participantsName;
    }

    public void setParticipantsName(String participantsName) {
        this.participantsName = participantsName;
    }

    public String getParticipantsNumber() {
        return participantsNumber;
    }

    public void setParticipantsNumber(String participantsNumber) {
        this.participantsNumber = participantsNumber;
    }
}
