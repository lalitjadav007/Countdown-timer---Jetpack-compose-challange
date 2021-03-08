package com.example.androiddevchallenge.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.purple200
import com.example.androiddevchallenge.ui.theme.teal200
import kotlinx.coroutines.delay


// Start building your app here!
@Composable
fun MyApp(
    time: LiveData<Int> = MutableLiveData(),
    minute: LiveData<Int> = MutableLiveData(),
    hour: LiveData<Int> = MutableLiveData(),
    state: MutableLiveData<Boolean> = MutableLiveData(),
    handleStart: (() -> Unit)? = null,
    reset: (() -> Unit)? = null,
    showReset: MutableLiveData<Boolean> = MutableLiveData()) {

    val stopMessageShown by showReset.observeAsState()

    Surface(color = MaterialTheme.colors.background, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
//        Text(text = "$timeString")

        Box(contentAlignment = Alignment.BottomEnd) {
            val painter = painterResource(id = R.drawable.ic_baseline_delete_24)
            ButtonStartReset(
                Modifier.size(60.dp),
                RoundedCornerShape(30.dp, 0.dp, 0.dp, 0.dp),
                painter, reset!!)
        }

        EditMessage(stopMessageShown == true)

        Box(contentAlignment = Alignment.BottomStart) {
            val imageState by state.observeAsState()
            val painter = if(imageState == true){
                painterResource(id = R.drawable.ic_baseline_pause_24)
            } else {
                painterResource(id = R.drawable.ic_baseline_play_arrow_24)
            }
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
    Card(modifier = modifier
        .clip(shape)
        .clickable(enabled = true, onClick = started), backgroundColor = teal200, shape = shape) {
        Box(
            Modifier
                .wrapContentSize()
                .padding(5.dp), contentAlignment = Alignment.Center) {
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditMessage(shown: Boolean) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            // Enters by sliding in from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            // Exits by sliding out from offset 0 to -fullHeight.
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top),
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp
        ) {
            Text(
                text = stringResource(R.string.reset_message),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}