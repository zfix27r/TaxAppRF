//package com.taxapprf.taxapp.ui.converter;
//
//import android.app.Application;
//
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//public class ConverterViewModelFactory implements ViewModelProvider.Factory {
//    private LifecycleOwner owner;
//
//
//
//    public ConverterViewModelFactory(LifecycleOwner owner) {
//        this.owner = owner;
//    }
//
//
//    @Override
//    public <T extends ViewModel> T create(Class<T> modelClass) {
//        return (T) new ConverterViewModel(owner);
//    }
//}
