/*
 * Copyright (C) 2020 PekinSOFT Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.northwind.loadmgr.model;

import java.time.LocalDate;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class Load {
    private String orderNumber;
    private String tripNumber;
    private String unitAssigned;
    private String referenceNumber;
    private String pickupNumber;
    private int trailerType;
    private String commodity;
    private int weight;
    private int pieces;
    private String bolNumber;
    private boolean tarped;
    private int tarpType;
    private boolean teamLoad;
    private boolean hazMat;
    private boolean cbd;
    private boolean ltl;
    private boolean twic;
    private boolean rampsRequired;
    private int miles;
    private double revenue;
    private String broker;
    private String brokerPhone;
    private String dispatcher;
    private String dispatcherPhone;
    private LocalDate bookedOn;

    public Load() {
        
    }
    
    public Load(String order, String trip) {
        this.orderNumber = order;
        this.tripNumber = trip;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getUnitAssigned() {
        return unitAssigned;
    }

    public void setUnitAssigned(String unitAssigned) {
        this.unitAssigned = unitAssigned;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getPickupNumber() {
        return pickupNumber;
    }

    public void setPickupNumber(String pickupNumber) {
        this.pickupNumber = pickupNumber;
    }

    public int getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(int trailerType) {
        this.trailerType = trailerType;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public boolean isTarped() {
        return tarped;
    }

    public void setTarped(boolean tarped) {
        this.tarped = tarped;
    }

    public int getTarpType() {
        return tarpType;
    }

    public void setTarpType(int tarpType) {
        this.tarpType = tarpType;
    }

    public boolean isTeamLoad() {
        return teamLoad;
    }

    public void setTeamLoad(boolean teamLoad) {
        this.teamLoad = teamLoad;
    }

    public boolean isHazMat() {
        return hazMat;
    }

    public void setHazMat(boolean hazMat) {
        this.hazMat = hazMat;
    }

    public boolean isCbd() {
        return cbd;
    }

    public void setCbd(boolean cbd) {
        this.cbd = cbd;
    }

    public boolean isLtl() {
        return ltl;
    }

    public void setLtl(boolean ltl) {
        this.ltl = ltl;
    }

    public boolean isTwic() {
        return twic;
    }

    public void setTwic(boolean twic) {
        this.twic = twic;
    }

    public boolean isRampsRequired() {
        return rampsRequired;
    }

    public void setRampsRequired(boolean rampsRequired) {
        this.rampsRequired = rampsRequired;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getBrokerPhone() {
        return brokerPhone;
    }

    public void setBrokerPhone(String brokerPhone) {
        this.brokerPhone = brokerPhone;
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getDispatcherPhone() {
        return dispatcherPhone;
    }

    public void setDispatcherPhone(String dispatcherPhone) {
        this.dispatcherPhone = dispatcherPhone;
    }

    public LocalDate getBookedOn() {
        return bookedOn;
    }

    public void setBookedOn(LocalDate bookedOn) {
        this.bookedOn = bookedOn;
    }
}
