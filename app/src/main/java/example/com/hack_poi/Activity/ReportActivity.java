package example.com.hack_poi.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import example.com.hack_poi.Adapter.DisplayDetailsAdapter;
import example.com.hack_poi.DataBase.DB;
import example.com.hack_poi.DataBase.DB_FULL_DETAILS;
import example.com.hack_poi.Model.FinalDetails;
import example.com.hack_poi.R;

public class ReportActivity extends AppCompatActivity {
    Context context;
    RecyclerView recycleViewList;
    Button btn_home;
    TextView txt_noRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        context = this;
        recycleViewList = findViewById(R.id.recycleViewList);
        btn_home = findViewById(R.id.btn_home);
        txt_noRecord = findViewById(R.id.txt_noRecord);

        DB db = new DB(context);
        int rowCount = DB_FULL_DETAILS.getDetailsRowCount(context);

        if (rowCount > 0) {
            String Query = "SELECT * FROM " + DB_FULL_DETAILS.ALL_DETAILS;

            List<FinalDetails> detailsList = db.getTxnRecord(Query);
            Log.e("List", detailsList.size()+"");
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewList.setLayoutManager(layoutManager);
            DisplayDetailsAdapter adapter = new DisplayDetailsAdapter(detailsList,context);
            recycleViewList.setAdapter(adapter);

        }else {
            recycleViewList.setVisibility(View.GONE);
            txt_noRecord.setVisibility(View.VISIBLE);
            /*NO Record found*/
        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
