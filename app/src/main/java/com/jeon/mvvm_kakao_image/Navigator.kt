package com.jeon.mvvm_kakao_image

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object Navigator {
    fun addFragmentToActivity(
        fragmentManager: FragmentManager,
        fragment: Fragment, frameId: Int, isBackStack: Boolean
    ) {

        try {
            if (frameId == -1) return
            if (fragmentManager.isDestroyed || fragment.isAdded) return
            val prev = fragmentManager.findFragmentById(frameId)
            val transaction = fragmentManager.beginTransaction()
            if (prev != null) {
                transaction.remove(prev)
            }
            transaction.replace(frameId, fragment)
            if (isBackStack) transaction.addToBackStack(null)
            transaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}