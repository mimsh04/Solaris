package in2000.team42.ui.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen (navController: NavHostController,modifier : Modifier = Modifier) {

    val gridState= rememberLazyGridState()

    Column(
        modifier=Modifier.fillMaxSize()
    ){
        Spacer(modifier=Modifier.padding(16.dp))

        Box(
            modifier=Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ){
            Column{
                Row(
                    modifier = Modifier.fillMaxWidth()

                ){
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "ProfilePicture",
                        tint=Color.Black,
                        modifier=Modifier.size(50.dp)

                    )

                    Spacer(modifier=Modifier.width(16.dp))

                    Column(
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text="John Doe",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold

                        )
                        Text(
                            text="Solar Enthusiast"
                        )

                    }


                }

            }



            Column{
                Text(
                    text="Anbefalte Produkter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 80.dp,bottom=10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )

                LazyVerticalGrid(
                    columns=GridCells.Fixed(2),
                    state=gridState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(2){index->
                        Box(
                            modifier=Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray)
                                .border(1.dp,Color.Gray,RoundedCornerShape(10.dp))
                        ){
                            Text(text="High Effiency panels",modifier=Modifier.align(Alignment.TopStart))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(Color.White)
                                    .clip(RoundedCornerShape(10.dp))
                            ){
                                Text(text="Total Savings")
                            }
                        }

                    }


                }

                Text(
                    text="Lurer du på noe?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 25.dp,bottom=10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )

                    LazyVerticalGrid(
                        columns=GridCells.Fixed(2),
                        state=gridState,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {
                        items(2){index->
                            val text= if(index==0) "Installasjons\nGuide" else "FAQs"

                            val icon= when(index){
                                0->Icons.Default.Build
                                1->Icons.Default.Info
                                else->Icons.Default.Edit
                            }



                            Box(
                                modifier=Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.dp,Color.Gray,RoundedCornerShape(10.dp))
                            ){
                                Row(
                                    modifier=Modifier.fillMaxWidth()
                                        .padding(top=30.dp,start=10.dp)
                                ){
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "ProfilePicture",
                                        tint=Color.Black,
                                        modifier=Modifier.size(30.dp)

                                    )
                                }
                                Text(
                                    text=text,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier=Modifier.padding(start=50.dp,top=30.dp)
                                )

                            }

                        }
                    }

                Text(
                    text="Solar Panel Data",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 25.dp,bottom=10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )

                Column(
                    modifier=Modifier.fillMaxWidth()
                ){
                    Text(
                        text="Energy Output",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(top = 25.dp)
                            .wrapContentWidth(Alignment.Start)
                    )

                    Text(
                        text="Battery Status",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(top=50.dp,bottom=10.dp)
                            .wrapContentWidth(Alignment.Start)
                    )

                }


















            }



        }












    }




}







