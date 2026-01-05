package com.example.officerdutymanagement.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.officerdutymanagement.model.DashboardStats;
import com.example.officerdutymanagement.repository.DashboardRepository;

public class DashboardViewModel extends ViewModel {
    
    private final DashboardRepository dashboardRepository;
    private final MutableLiveData<Integer> lateCheckInCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> requestForAbsenceCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> presentTodayCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public DashboardViewModel() {
        dashboardRepository = DashboardRepository.getInstance();
        observeRepository();
    }
    
    private void observeRepository() {
        dashboardRepository.getDashboardStats().observeForever(stats -> {
            if (stats != null) {
                lateCheckInCount.setValue(stats.getLateCheckInCount());
                requestForAbsenceCount.setValue(stats.getAbsenceRequestCount());
                presentTodayCount.setValue(stats.getPresentTodayCount());
            }
        });
        
        dashboardRepository.getIsLoading().observeForever(isLoading::setValue);
        dashboardRepository.getErrorMessage().observeForever(errorMessage::setValue);
    }
    
    public LiveData<Integer> getLateCheckInCount() {
        return lateCheckInCount;
    }
    
    public LiveData<Integer> getRequestForAbsenceCount() {
        return requestForAbsenceCount;
    }
    
    public LiveData<Integer> getPresentTodayCount() {
        return presentTodayCount;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void loadDashboardStats() {
        dashboardRepository.loadDashboardStats();
    }
    
    public void loadSupervisorDashboardStats() {
        dashboardRepository.loadSupervisorDashboardStats();
    }
    
    public LiveData<Integer> getActiveDutyAssignmentsCount() {
        MutableLiveData<Integer> activeDutyCount = new MutableLiveData<>(0);
        dashboardRepository.getDashboardStats().observeForever(stats -> {
            if (stats != null && stats.getActiveDutyAssignmentsCount() != null) {
                activeDutyCount.setValue(stats.getActiveDutyAssignmentsCount());
            }
        });
        return activeDutyCount;
    }
    
    public LiveData<Integer> getTotalOfficersCount() {
        MutableLiveData<Integer> totalOfficersCount = new MutableLiveData<>(0);
        dashboardRepository.getDashboardStats().observeForever(stats -> {
            if (stats != null && stats.getTotalOfficersCount() != null) {
                totalOfficersCount.setValue(stats.getTotalOfficersCount());
            }
        });
        return totalOfficersCount;
    }
}

