package com.example.scheduler_practice

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    val days = arrayOf("", "월","화", "수", "목", "금", "토", "일") // 요일 배열
    val times = Array(24,{i -> (i.toString() + "시")}) // Array 메소드를 이용해 시간 배열 생성
    var schedulerData = mutableMapOf( // 스케줄러에 보여줄 일정을 모아놓는 Map
        22 to SchedulerData(22, "시스템 소프트웨어", "윤상원", "미래관 4층 45호실"),
        28 to SchedulerData(28, "모바일 프로그래밍", "윤상원", "미래관 6층 11호실")
    )
    //testData
    var cells = mutableMapOf<Int, View>() // 스케줄러를 구성하는 각각의 셀을 모아놓는 Map
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid: GridLayout = findViewById(R.id.gridLayout) // GridLayout 가져옴
        grid.columnCount = 8
        grid.rowCount = 49 // 행 및 열의 크기 지정

        createCell(100,100,0,0,grid) // (0,0) 위치에 크기 100*100짜리 셀 하나 생성

        for (i : Int in 1 until grid.columnCount){
            val layout = createCell(500, 100, i,0, grid) // (i,0)위치에 500*100짜리 셀 생성
            val text = TextView(this) // 텍스트를 넣을 TextView 생성
            text.textSize = 10f // 텍스트 사이즈 설정, 값이 float형이어야 함.
            text.text = days[i] // days 배열에 들어있는 값을 가져다 TextView에 넣어줌
            text.gravity = Gravity.CENTER // gravity의 값을 center로 한다.
            text.layoutParams = ConstraintLayout.LayoutParams( // LayoutParams()의 생성자는 너비와 높이를 매개변수로 입력받는다.
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layout.addView(text)
        }

        for (i : Int in 1 until grid.rowCount){
            val layout = createCell(100, 300, 0, i, grid) // (0,i) 위치에 100*300짜리 셀 생성
            val text = TextView(this)
            text.textSize = 10f
            text.text =
                if (i % 2 != 0) times[(i-1)/2] else times[(i-1)/2] + " 30분"
            text.gravity = Gravity.CENTER
            text.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layout.addView(text)
        }

        for (i : Int in 1 until grid.rowCount){
            for(j : Int in 1 until grid.columnCount){
                val layout = createCell(500, 300, j, i, grid) // (j,i) 위치에 500*300짜리 셀 생성
                val cell : View = layoutInflater.inflate(R.layout.scheduler_item, layout) // LayoutInflater의 inflate 메소드를 이용해 레이아웃을 다른 레이아웃에 삽입한다. 첫번째 파라미터는 객체화할 레이아웃, 두번째 파라미터는 삽입할 위치이다. cell 변수에는 inflate로 객체화된 View가 저장된다.
                val idx = ((i - 1) * (grid.columnCount - 1)) + (j - 1) // idx를 계산해준다. idx는 셀의 순서를 의미한다.
                cells[idx] = cell // 스케줄러를 구성하는 셀들을 모아놓는 map에 idx를 key값으로 하여 저장한다.
                if(schedulerData.containsKey(idx)){ // 내가 지금 만드는 셀에 보여줄 일정이 schedulerData에 저장되어 있는지 검색한다.
                    val data = schedulerData[idx] // 일정을 data 변수에 담는다.
                    cell.findViewById<TextView>(R.id.scheduler_item_title).text = data?.title // cell 내부에 있는 scheduler_item_title라는 id를 가진 TextView의 text를 schedulerData에 저장되어있는 title 값으로 바꿔준다.
                    cell.findViewById<TextView>(R.id.scheduler_item_username).text = data?.user // 위와 동일
                    cell.findViewById<TextView>(R.id.scheduler_item_location).text = data?.location // 위와 동일
                    cell.setOnClickListener{ // 셀이 눌렸을 때의 event를 정의해 준다.
                        val view = layoutInflater.inflate(R.layout.dialog,null) // dialog 레이아웃을 객체화한다. view 변수에 객체화한 View를 저장한다.
                        view.findViewById<EditText>(R.id.dialog_title).setText(schedulerData[idx]?.title) // 셀을 눌러 dialog가 떴을 때 title을 입력하는 EditText의 내용을 schedulerData에 들어있는 title값으로 세팅해준다.
                        view.findViewById<EditText>(R.id.dialog_user).setText(schedulerData[idx]?.user) // 위와 동일
                        view.findViewById<EditText>(R.id.dialog_location).setText(schedulerData[idx]?.location) // 위와 동일
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity) // alertDialog를 만들기 위한 builder 객체를 생성한다. 매개변수에 그냥 this를 써주게 되면 cell.setOnClickListener를 가리키기 때문에(setOnClickListener가 인터페이스이기 때문에) this@MainActivity로 MainActivity를 가리켜준다.
                        builder
                            .setView(view) // 만들어놓은 view를 builder 객체에 올려준다.
                            .setPositiveButton( // 긍정의 의미를 나타내는 버튼을 세팅해 준다.
                                "수정", // 버튼의 text 내용
                                DialogInterface.OnClickListener{dialog, index -> // 눌렀을 때의 동작을 정의해준다.
                                    schedulerData[idx]?.title = // schedulerData에 저장되어있는 title 값을 EditText에 입력된 값으로 바꿔준다.
                                        view.findViewById<EditText>(R.id.dialog_title).text.toString()
                                    schedulerData[idx]?.user = // 위와 동일
                                        view.findViewById<EditText>(R.id.dialog_user).text.toString()
                                    schedulerData[idx]?.location = // 위와 동일
                                        view.findViewById<EditText>(R.id.dialog_location).text.toString()
                                    refreshCell(schedulerData)
                                })
                            .setNegativeButton( // 부정의 의미를 나타내는 버튼을 세팅해준다.
                                "취소",
                                DialogInterface.OnClickListener{ dialog, index ->
                                    dialog.cancel()
                                })
                            .create().show() // alertDialog를 생성하고 보여준다.
                    }
                    cell.setOnLongClickListener{ // 셀이 길게 눌렸을 때 event를 정의해준다.
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setMessage("삭제하시겠습니까?") // alertDialog에서 보여줄 내용을 세팅해준다.
                            .setPositiveButton(
                                "삭제",
                                DialogInterface.OnClickListener{ dialog, index ->
                                    schedulerData.remove(idx) // 눌린 셀에 있는 일정에 대한 정보를 schedulerData에서 삭제한다.
                                    refreshCell(schedulerData)
                                })
                            .setNegativeButton(
                                "취소",
                                DialogInterface.OnClickListener{ dialog, index ->
                                    dialog.cancel()
                                })
                            .create().show()
                        return@setOnLongClickListener true // setOnClickListener 메소드는 boolean형의 리턴값을 갖는다. 길게 누르고 있을 때 onLongclickListener가 작동하고 손가락을 떼는 순간 onClickListener가 작동한다. 이때 리턴값을 true로 해주면 onClickLisener가 작동하지 않고(이벤트 완료), false면 onClickLisener가 작동한다(다음 이벤트 계속 진행).
                    }
                } else{
                    
                }
            }
        }
    }

    private fun createCell(w : Int, h : Int, c : Int, r: Int, grid:GridLayout) : ConstraintLayout{
        val layout = ConstraintLayout(this) // constraintlayout 하나를 생성해서 layout 변수에 저장
        val param: GridLayout.LayoutParams = GridLayout.LayoutParams() // LayoutParams는 부모 레이아웃 안에서 View가 어떻게 배치될지 정의하는 속성이다.
        param.setGravity(Gravity.CENTER) // layout_gravity의 값을 center로 한다. (gravity의 값은 View.setGravity()로 지정할 수 있다.)
        param.columnSpec = GridLayout.spec(c) // 해당 셀을 c번째 열에 놓는다
        param.rowSpec = GridLayout.spec(r) // 해당 셀을 r번째 행에 놓는다
        param.width = w // 셀의 너비 w
        param.height = h // 셀의 높이 h
        layout.layoutParams = param // param 변수에 저장된 내용을 layout 변수 LayoutParams에 저장
        grid.addView(layout) // gridlayout에 layout을 추가한다.
        return layout // 만든 셀을 리턴한다.
    }

    private fun refreshCell(datas: MutableMap<Int, SchedulerData>){ // 매개변수로는 idx를 키로 하고 일정의 정보를 값으로 갖는 map을 넘겨받는다.
        val grid: GridLayout = findViewById(R.id.gridLayout) // activity_main.xml에 정의되어있는 gridlayout을 가져온다.

        for(i : Int in 1 until grid.rowCount){
            for(j : Int in 1 until grid.columnCount){
                val idx = ((i - 1) * (grid.columnCount - 1)) + (j - 1) // 셀의 순서를 의미하는 idx를 계산해준다.
                val cell : View? = cells[idx] //cells에 저장되어있는 View를 꺼내와 cell 변수에 저장한다.
                if(datas.containsKey(idx)){
                    val data = datas[idx]
                    cell?.findViewById<TextView>(R.id.scheduler_item_title)?.text = data?.title // cells에서 꺼내온 View의 TextView 내용을 바꿔준다.
                    cell?.findViewById<TextView>(R.id.scheduler_item_username)?.text = data?.user // 위와 동일
                    cell?.findViewById<TextView>(R.id.scheduler_item_location)?.text = data?.location // 위와 동일
                } else{ // 매개변수로 넘겨받은 map에 idx를 key로 갖는 item이 없는 경우
                    cell?.findViewById<TextView>(R.id.scheduler_item_title)?.text = "" // cells에서 꺼내온 View의 TextView 내용을 ""으로 바꿔준다.
                    cell?.findViewById<TextView>(R.id.scheduler_item_username)?.text = ""
                    cell?.findViewById<TextView>(R.id.scheduler_item_location)?.text = ""
                }
            }
        }
    }
}