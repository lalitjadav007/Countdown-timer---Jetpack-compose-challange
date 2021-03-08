/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.lifecycleScope
import com.example.androiddevchallenge.ui.home.MyApp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var _time = MutableLiveData<Long>()
    private val time: LiveData<Long> = _time

    private val second: LiveData<Int> = Transformations.map(time){
        (it % 60).toInt()
    }
    private val minute: LiveData<Int> = Transformations.map(time){
        ((it / 60) % 60).toInt()
    }
    private val hour: LiveData<Int> = Transformations.map(time){
        ((it / 60 / 60)  % 60).toInt()
    }

    init {
        _time.value = 0
    }
    private var started = MutableLiveData<Boolean>()
    private var showResetMessage = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(second, minute, hour, started, { startOrPauseTimer() }, {
                    started.value = false
                    _time.value = 0
                    lifecycleScope.launch {
                        showMessage()
                    }
                }, showResetMessage)
            }
        }

        started.observe(this, Observer {
            if (it == null) return@Observer
            if(it){
                assignTime()
            }
        })
    }

    private fun showMessage() {
        lifecycleScope.launch {
            showResetMessage.value = showResetMessage.value != true
            delay(1000)
            showResetMessage.value = showResetMessage.value != true
            _time.value = 0
        }
    }

    private fun startOrPauseTimer(){
        started.value = started.value != true
    }

    private fun assignTime() {
        lifecycleScope.launch {
            incrementTime()
        }
    }

    private suspend fun incrementTime(){
        delay(1000)
        if (_time.value != null) {
            _time.value = _time.value as Long + 1L
        }
        if(started.value == true){
            assignTime()
        }
    }
}


