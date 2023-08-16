package com.taxapprf.taxapp.ui

import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.view.ActionMode

interface BaseActionModeCallback : ActionMode.Callback {
    var menuInflater: MenuInflater
    var menuId: Int

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menuInflater.inflate(menuId, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {}
}