// MainActivity.kt
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.example.myapplication.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private val gridSize = 5 // 5x5 grid
    private val cellViews = Array(gridSize) { arrayOfNulls<View>(gridSize) }
    private val correctCells = mutableListOf<Pair<Int, Int>>()
    private val displayTime: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        initializeGrid()
        displayRandomCells()
    }

    private fun initializeGrid() {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val view = View(this).apply {
                    setBackgroundColor(Color.WHITE)
                    layoutParams = GridLayout.LayoutParams().apply {
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        width = GridLayout.LayoutParams.WRAP_CONTENT
                        setMargins(5)
                        rowSpec = GridLayout.spec(i, 1f)
                        columnSpec = GridLayout.spec(j, 1f)
                    }
                    setOnClickListener { cellClicked(it) } // Đặt sự kiện click ở đây
                }
                gridLayout.addView(view)
                cellViews[i][j] = view
            }
        }
    }

    private fun displayRandomCells() {
        correctCells.clear()
        val randomIndices = List(gridSize) { Random.nextInt(gridSize) }
        randomIndices.forEach { index ->
            val row = index / gridSize
            val col = index % gridSize
            correctCells.add(Pair(row, col))
            cellViews[row][col]?.setBackgroundColor(Color.YELLOW)
        }

        Handler().postDelayed({
            correctCells.forEach { (row, col) ->
                cellViews[row][col]?.setBackgroundColor(Color.WHITE)
            }
        }, displayTime)
    }

    fun cellClicked(view: View) {
        // This method will be triggered when a cell is clicked.
        val handler = Handler(mainLooper)
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (cellViews[i][j] == view) {
                    if (Pair(i, j) in correctCells) {
                        view.setBackgroundColor(Color.GREEN)
                    } else {
                        view.setBackgroundColor(Color.RED)
                    }
                    // Disable all cells after a click for the rest of the display time
                    disableAllCells()
                    // Wait for the rest of the display time plus a little extra before resetting the game
                    handler.postDelayed({
                        resetGame()
                    }, displayTime - System.currentTimeMillis() % displayTime + 500)
                    return
                }
            }
        }
    }

    private fun disableAllCells() {
        for (row in cellViews) {
            for (cell in row) {
                cell?.isEnabled = false
            }
        }
    }

    private fun resetGame() {
        correctCells.clear()
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                cellViews[i][j]?.apply {
                    setBackgroundColor(Color.WHITE)
                    isEnabled = true
                }
            }
        }
        displayRandomCells()
    }
}
