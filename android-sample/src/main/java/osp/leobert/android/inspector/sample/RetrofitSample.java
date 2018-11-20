package osp.leobert.android.inspector.sample;

import okhttp3.OkHttpClient;
import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.sample.bean.Person;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public final class RetrofitSample {

    public interface FooService {
        @GET("bar")
        Person<String, String> getPerson();
    }

    public static FooService createRetrofit() {
        Inspector inspector = new Inspector.Builder()
                .add(SampleFactory.create())
                .build();
        InspectorConverterFactory.ValidationExceptionCallback callback = (type, validationException) -> {
            // This response didn't pass validation!

            // You could log it
            System.out.println("Validation exception: "
                    + type
                    + ". Error: "
                    + validationException.getMessage());

            // Or throw it to fail hard
            throw validationException;

            // Or wrap in an IOException to drop it on the floor
            //throw new IOException(validationException);
        };
        FooService service = new Retrofit.Builder().callFactory(new OkHttpClient())
                .baseUrl("https://example.com/")
                .validateEagerly(true)
                .addConverterFactory(new InspectorConverterFactory(inspector, callback))
                .build()
                .create(FooService.class);

        return service;
    }
}
