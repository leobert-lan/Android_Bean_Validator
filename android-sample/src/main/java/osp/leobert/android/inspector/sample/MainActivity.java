package osp.leobert.android.inspector.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.sample.bean.BarIValidator;
import osp.leobert.android.inspector.sample.bean.Foo;
import osp.leobert.android.inspector.validators.ClassValidator;
import osp.leobert.android.inspector.validators.Validator;

public class MainActivity extends AppCompatActivity {
    Foo foo = new Foo("bar");
    Inspector inspector = new Inspector.Builder()
//            .add(GenerateValidator.FACTORY)
            .add(ClassValidator.FACTORY)
            .add(Foo.class, Foo.FooValidatorBy.class, new Foo.FooValidator())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(v -> {
            try {
                Log.e("lmsg", "a");
                Validator<Foo> validator = inspector.validator(Foo.class, Foo.FooValidatorBy.class);

                boolean b = validator.isValid(foo);
                Toast.makeText(v.getContext(), String.valueOf(b), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("lmsg", e.toString());
                onDestroy();
            }
        });


    }

    @Override
    protected void onDestroy() {
        Log.e("lmsg", "onDestroy");
        finish();
        super.onDestroy();
    }

    @Override
    public void finish() {
        Log.e("lmsg", "finish");
        super.finish();
    }
}
