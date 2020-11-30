package com.ninjeng.bankaksassignmentv2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.PrecomputedText;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private RequestQueue mQueue;
    List<EditText> editTextList;
    LinearLayout linearLayout;
    List<String> hints= null;
    ArrayList<String>  regex= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.Spinner);
        linearLayout = findViewById(R.id.linearList);
        mQueue = Volley.newRequestQueue(this);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Task 1");
        arrayList.add("Task 2");
        arrayList.add("Task 3");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] type = spinner.getSelectedItem().toString().split(" ");
                CreateLayoutFromJSON(type[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void CreateLayoutFromJSON(final String type)
    {
        editTextList = new ArrayList<>();
        if((linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();
        final String base_url = "https://api-staging.bankaks.com/task/"+type;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, base_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("result");
                    TextView textView = new TextView(MainActivity.this);
                    textView.setText(jsonObject.getString("screen_title"));
                    textView.setTextSize(20f);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 10, 0, 5);
                    textView.setLayoutParams(params);
                    linearLayout.addView(textView);
                    final int noOfTextFields =jsonObject.getInt("number_of_fields");
                    final JSONArray fields  =jsonObject.getJSONArray("fields");
                    hints = new ArrayList<>();
                    regex = new ArrayList<>();
                    editTextList.clear();

                    for (int i = 0;i < noOfTextFields; i++)
                    {
                        JSONObject field = fields.getJSONObject(i);
                        JSONObject ui_type = field.getJSONObject("ui_type");
                        JSONObject data_type = field.getJSONObject("type");
                        if(ui_type.getString("type").equals("textfield"))
                        {
                            createEditText(field,data_type,i);

                        }
                        else if(ui_type.getString("type").equals("dropdown"))
                        {
                            createSpinner(ui_type,field,i);

                        }

                    }
                    Button btnCalc = createButton();
                    btnCalc.setGravity(Gravity.CENTER_HORIZONTAL);
                    btnCalc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            validation(type,hints,regex);
                        }

                    });
                    linearLayout.addView(btnCalc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
    }
    private Button createButton()
    {
        Button btn = new Button(MainActivity.this);
        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setText(getString(R.string.btn_string));
        return btn;
    }

    private void createEditText(JSONObject field,JSONObject data_type, int i ) throws JSONException {
        EditText editText = new EditText(MainActivity.this);
        editText.setHint(field.getString("placeholder"));
        if(data_type.getString("data_type").equals("string"))
        {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else if (data_type.getString("data_type").equals("int"))
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setId(i);
        editText.setBackgroundResource(R.drawable.input_design);

        editTextList.add(editText);
        hints.add(field.getString("hint_text"));
        regex.add(field.getString("regex"));
        linearLayout.addView(editText);
    }
    private void createSpinner(JSONObject ui_type,JSONObject field,int i) throws JSONException {

        TextView textView2 = new TextView(MainActivity.this);
        textView2.setTextSize(14);
        textView2.setText(field.getString("placeholder"));
        Spinner spinner = new Spinner(MainActivity.this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        JSONArray values = ui_type.getJSONArray("values");
        List<String> arr = new ArrayList<>();
        for(int v = 0; v<values.length();v++)
        {
            JSONObject months = values.getJSONObject(v);
            arr.add(months.getString("name"));
        }
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arr);
        spinner.setAdapter(arrAdapter);
        spinner.setId(i);
        linearLayout.addView(textView2);
        linearLayout.addView(spinner);
    }
    private void validation(String type, List<String> hints,List<String> regex){
        switch (type) {
            case "1": {
                EditText sc_no = editTextList.get(0);
                String sc_no_string = editTextList.get(0).getText().toString();
                EditText customer_id = editTextList.get(1);
                String customer_id_string = editTextList.get(1).getText().toString();

                if (sc_no_string.length() == 0 || sc_no_string.length()<7) {
                    sc_no.requestFocus();
                    sc_no.setError(hints.get(0));
                } else if (customer_id_string.length() == 0 || customer_id_string.length()<5) {
                    customer_id.requestFocus();
                    customer_id.setError(hints.get(1));
                } else {
                    Toast.makeText(this, getString(R.string.strSuccess), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case "2": {
                EditText customer_id = editTextList.get(0);
                String customer_id_string = editTextList.get(0).getText().toString();

                if (customer_id_string.length() == 0 || customer_id_string.length()<3) {
                    customer_id.requestFocus();
                    customer_id.setError(hints.get(0));
                } else {
                    Toast.makeText(this, getString(R.string.strSuccess), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case "3":
                EditText customer_email = editTextList.get(0);
                String customer_email_string = editTextList.get(0).getText().toString().trim();
                EditText customer_phone = editTextList.get(1);
                String customer_phone_string = editTextList.get(1).getText().toString().trim();
                EditText name = editTextList.get(2);
                String name_string = editTextList.get(2).getText().toString();

                //Solution 1
                //Getting the values of regex from the api and Using Pattern.match() on it.
//                Pattern emailPattern = Pattern.compile(regex.get(0));
//                Pattern phonePattern = Pattern.compile(regex.get(1));
//
//                if(!emailPattern.matcher(customer_email_string).matches())
//                {
//                    customer_email.requestFocus();
//                    customer_email.setError("eg("+hints.get(0)+")");
//                }
//                else if(!phonePattern.matcher(customer_phone_string).matches())
//                {
//                    customer_phone.requestFocus();
//                    customer_phone.setError("eg("+hints.get(1)+")");
//                }

                //Solution 2
                //Coping the regex direct from the API and managing the missing brackets.

//                Pattern patternEmail = Pattern.compile("/^(([^<>()[\\\\.,;:s@\\\"]]+(.[^<>()[\\\\.,;:s@\\\"]]+)*)\n" +
//                        "|(\\\".+\\\"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|\n" +
//                        "(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$");
//                Pattern patternPhone = Pattern.compile("^[6-9]d{9}$");
//
//                if(!customer_email_string.matches(String.valueOf(patternEmail)))
//                {
//                    customer_email.requestFocus();
//                    customer_email.setError("eg("+hints.get(0)+")");
//                }
//                else if(!customer_phone_string.matches(String.valueOf(patternPhone)))
//                {
//                    customer_phone.requestFocus();
//                    customer_phone.setError("eg("+hints.get(1)+")");
//                }
//
//
                //Solution 3
                //Using build in email and phone regex.

                if(!Patterns.EMAIL_ADDRESS.matcher(customer_email_string).matches()) {
                    customer_email.requestFocus();
                    customer_email.setError("eg("+hints.get(0)+")");
                }
                else if (!Patterns.PHONE.matcher(customer_phone_string).matches()) {
                    customer_phone.requestFocus();
                    customer_phone.setError("eg("+hints.get(1)+")");
                }

                else if(customer_phone_string.length()==0)
                {
                    customer_phone.requestFocus();
                    customer_phone.setError("eg("+hints.get(1)+")");
                }
                else if (name_string.length() == 0) {
                    name.requestFocus();
                    name.setError("eg("+hints.get(2)+")");
                } else {
                    Toast.makeText(this, getString(R.string.strSuccess), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}