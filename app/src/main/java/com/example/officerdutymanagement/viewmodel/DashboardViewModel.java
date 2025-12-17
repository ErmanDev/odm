package com.example.officerdutymanagement.viewmodel;

import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    
    // Static metrics for now - can be replaced with LiveData later
    private int lateCheckInCount = 4;
    private int requestForAbsenceCount = 1;
    private int presentTodayCount = 25;
    
    public DashboardViewModel() {
    }
    
    public int getLateCheckInCount() {
        return lateCheckInCount;
    }
    
    public int getRequestForAbsenceCount() {
        return requestForAbsenceCount;
    }
    
    public int getPresentTodayCount() {
        return presentTodayCount;
    }
    
    // Future: These can be replaced with LiveData when API integration is added
    // private MutableLiveData<Integer> lateCheckInCount = new MutableLiveData<>();
    // private MutableLiveData<Integer> requestForAbsenceCount = new MutableLiveData<>();
    // private MutableLiveData<Integer> presentTodayCount = new MutableLiveData<>();
}

