package com.example.animatedmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.animatedmenu.ui.theme.AnimatedMenuTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimatedMenuTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        var open by remember { mutableStateOf(false) }
                        val items = listOf(
                            R.drawable.like,
                            R.drawable.dislike,
                            R.drawable.heart,
                            R.drawable.thanks
                        )
                        AnimatedMenu(
                            open = open,
                            items = items,
                            menuDirection = MenuDirection.Horizontal
                        ) { index, item, modifier ->
                            Image(
                                painter = painterResource(id = item),
                                contentDescription = index.toString(),
                                modifier = modifier
                                    .clickable { open = false }
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CutCornerShape(topStart = 12.dp)),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = 12.dp,
                                            topStart = 12.dp,
                                            bottomEnd = 12.dp,
                                            bottomStart = 0.dp
                                        )
                                    )
                                    .clickable { open = !open },
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Click me to open menu ! the menu will open after 300ms ! how many items you have ?"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> AnimatedMenu(
    open: Boolean,
    items: List<T>,
    openDuration: Int = 300,
    itemDuration: Int = 150,
    itemAnimDelay: Long = (openDuration - 100).toLong(),
    itemSize: Int = 30,
    parentSize: Int = itemSize + 20,
    backgroundColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
    menuEnterAnim: EnterTransition = scaleIn(tween(openDuration)),
    menuExitAnim: ExitTransition = scaleOut(tween(openDuration)),
    menuDirection: MenuDirection = MenuDirection.Horizontal,
    itemBuilder: @Composable (Int, T, Modifier) -> Unit
) {
    val parentModifier = Modifier
        .clip(CircleShape)
        .background(backgroundColor)
        .size(
            height = if (menuDirection == MenuDirection.Horizontal) parentSize.dp else (items.count() * (itemSize + 16)).dp,
            width = if (menuDirection == MenuDirection.Horizontal) (items.count() * (itemSize + 16)).dp else parentSize.dp
        )
    val content: @Composable (index: Int, item: T) -> Unit = { index, item ->
        var size by remember { mutableStateOf(0.dp) }
        val animDuration =
            (items.count() * itemDuration) - (((items.count() - 1) - index) * itemDuration)
        val sizeAnimate = animateDpAsState(
            targetValue = size, animationSpec = tween(animDuration),
            label = ""
        )
        LaunchedEffect(key1 = open) {
            delay(itemAnimDelay)
            size = itemSize.dp
        }
        itemBuilder(index, item, Modifier.size(sizeAnimate.value))
    }
    AnimatedVisibility(visible = open, enter = menuEnterAnim, exit = menuExitAnim) {
        if (menuDirection == MenuDirection.Horizontal) {
            Row(
                modifier = parentModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, item ->
                    content(index, item)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        } else {
            Column(
                modifier = parentModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, item ->
                    content(index, item)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

enum class MenuDirection {
    Vertical, Horizontal
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AnimatedMenuTheme {
    }
}