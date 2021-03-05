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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.lifecycleScope
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.purple200
import com.example.androiddevchallenge.ui.theme.teal200
import kotlinx.coroutines.cancel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(second, minute, hour, { startOrPauseTimer() }, { _time.value = 0})
            }
        }

        started.observe(this, Observer {
            if (it == null) return@Observer
            if(it){
                assignTime()
            }
        })
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

// Start building your app here!
@Composable
fun MyApp(
    time: LiveData<Int> = MutableLiveData(),
    minute: LiveData<Int> = MutableLiveData(),
    hour: LiveData<Int> = MutableLiveData(),
    handleStart: (() -> Unit)? = null,
    reset: (() -> Unit)? = null
) {
    Surface(color = MaterialTheme.colors.background, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
//        Text(text = "$timeString")

        Box(contentAlignment = Alignment.BottomEnd) {
            val painter = painterResource(id = R.drawable.ic_baseline_delete_24)
            ButtonStartReset(
                Modifier.size(60.dp),
                RoundedCornerShape(30.dp, 0.dp, 0.dp, 0.dp),
                painter, reset!!
            )
        }

        Box(contentAlignment = Alignment.BottomStart) {
            val painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24)
            ButtonStartReset(
                Modifier.size(60.dp),
                RoundedCornerShape(0.dp, 30.dp, 0.dp, 0.dp),
                painter, handleStart!!
            )
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            SecondView(
                Modifier
                    .width(180.dp)
                    .height(180.dp), time = time)

            Spacer(modifier = Modifier.size(20.dp))
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.clip(shape = RoundedCornerShape(50.dp))) {
//                val painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24)
//                Image(painter = painter , contentDescription = "Start Counter")
//            }

                MinuteView(
                    Modifier
                        .width(77.dp)
                        .height(60.dp), shape = AbsoluteRoundedCornerShape(30.dp, 0.dp, 0.dp, 30.dp), time = minute)

                Spacer(modifier = Modifier.size(6.dp))

                MinuteView(
                    Modifier
                        .width(77.dp)
                        .height(60.dp), shape = AbsoluteRoundedCornerShape(0.dp, 30.dp, 30.dp, 0.dp), time = hour)

//            Button(onClick = { /*TODO*/ }, modifier = Modifier.clip(shape = RoundedCornerShape(50.dp))) {
//                val painter = painterResource(id = R.drawable.ic_baseline_delete_24)
//                Image(painter = painter , contentDescription = "Reset Counter")
//            }
            }
        }
    }
}

@Composable
fun SecondView(modifier: Modifier = Modifier, time: LiveData<Int> = MutableLiveData()){
    val timeString by time.observeAsState()
    Card(modifier, shape = AbsoluteRoundedCornerShape(90.dp, 90.dp, 90.dp, 90.dp), elevation = 8.dp, backgroundColor = purple200) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = String.format("%02d", timeString), style = MaterialTheme.typography.h1)
        }
    }
}

@Composable
fun MinuteView(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(0.dp), time: LiveData<Int> = MutableLiveData()){
    val timeString by time.observeAsState()
    Card(modifier, shape = shape, elevation = 8.dp, backgroundColor = purple200) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = String.format("%02d", timeString), style = MaterialTheme.typography.h4)
        }
    }
}

@Composable
fun ButtonStartReset(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(0.dp),
    painter: Painter,
    started: () -> Unit
){
    Card(modifier = modifier.clip(shape).clickable(enabled = true, onClick = started), backgroundColor = teal200, shape = shape) {
        Box(Modifier.wrapContentSize().padding(5.dp), contentAlignment = Alignment.Center) {
            Image(painter = painter, modifier = Modifier , contentDescription = "Reset Counter")
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

//@Preview("Dark Theme", widthDp = 360, heightDp = 640)
//@Composable
//fun DarkPreview() {
//    MyTheme(darkTheme = true) {
//        MyApp()
//    }
//}
