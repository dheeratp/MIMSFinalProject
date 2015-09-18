package citytrail.pedometer;


import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class AnimationView extends View {

    Paint paint;
    Paint paint2;

    Bitmap bm;
    Bitmap bmChild;
    int bmparent_offsetX, bmparent_offsetY;
    int bmchild_offsetX, bmchild_offsetY;


    Path animPathParent;
    Path animPathChild;
    PathMeasure pathMeasureParent;
    PathMeasure pathMeasureChild;
    float pathLengthParent;
    float pathLengthChild;

    float step;   //distance each step
    float distance;  //distance moved

    float[] pos;
    float[] tan;

    Matrix matrix;
    Canvas canvas;
    Bitmap alteredBitmap;
    Bitmap resizedBitmap;
    private static final String TAG = "Pedometer";
    float x1, y1, x2, y2;
    SharedPreferences mState;
    //values for child
    float stepChild;
    float distanceChild;
    float[] posChild;
    float[] tanChild;

    Matrix matrixChild;
    float childx1, childy1, childx2, childy2;


    public AnimationView(Context context) {
        super(context);
        Log.i("Dheera - ","Inside AnimationView constructor");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("Dheera - ","Inside AnimationView constructor 2 attr");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("Dheera - ","Inside AnimationView constructor 3 attr");
        initMyView(context);
    }

    public void initMyView(Context context){

        //
    	Log.i(TAG, "[ACTIVITY] Inside initMyView function");
  

//        Drawable myDrawable = getResources().getDrawable(R.drawable.mapitaly);
//        alteredBitmap = ((BitmapDrawable) myDrawable).getBitmap();
//
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width=size.x;
//        int height=size.y;
////        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
////        float dp = 350f;
////        float fpixels = metrics.density * dp;
////        int pixels = (int) (fpixels + 0.5f);
//        resizedBitmap = Bitmap.createScaledBitmap(alteredBitmap, 3*width/4, height, false);



        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(70);
        paint.setStyle(Paint.Style.STROKE);

        //paint object for main line
        paint2 = new Paint();
        paint2.setColor(Color.parseColor("#E0555B")); //Color of pizza on the map is #E0555B
        paint2.setStrokeWidth(70);
        paint2.setStyle(Paint.Style.STROKE);

        //avatar for parent
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.pet2);
        bmparent_offsetX = bm.getWidth()/2;
        bmparent_offsetY = bm.getHeight()/2;
        //avatar for child
        bmChild = BitmapFactory.decodeResource(getResources(), R.drawable.pet1);
        bmchild_offsetX = bmChild.getWidth()/2;
        bmchild_offsetY = bmChild.getHeight()/2;

        animPathParent = new Path();
        animPathParent.moveTo(615, 1400);
      //  animPath.lineTo(740, 1270);
        //animPath.lineTo(1330, 1270);
        
        //animPath.close();

       

        //Toast.makeText(getContext(), "pathLength: " + pathLengthParent, Toast.LENGTH_LONG).show();

        step = 5;
        distance = 0;
        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();
        
        //values for child
        
        animPathChild = new Path();
        animPathChild.moveTo(785, 1400);
        
        stepChild = 5;
        distanceChild = 0;
        posChild = new float[2];
        tanChild = new float[2];
       
        matrixChild = new Matrix();
         Log.i("!!!!!!!!!!Dheera - ", "bm height="+bm.getHeight()+" Parent step count =" + Pedometer.mStepValue +" Child step count ="+Pedometer.childstepcount);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    	//VALUES FOR PARENT
        x1=615f;
    	y1=1400f;
    	x2=615f;
    	
    	if(Pedometer.mStepValue<100)
    	{
     		float temp=Float.parseFloat(Integer.toString(Pedometer.mStepValue))*11;
    		y2=y1-temp;
    	}
    	
    	canvas.drawLine(x1,y1,x2,y2,paint2);
    	
    	
    	//VALUES FOR CHILD
    	childx1=785f;
     	childy1=1400f;
     	childx2=785f;
     	if(Pedometer.childstepcount<100)
    	{
     		float tempchild=Float.parseFloat(Integer.toString(Pedometer.childstepcount))*11;
     		childy2=childy1-tempchild;
    	}
     	
    
    	
    	animPathParent.lineTo(x2, y2);
    	animPathChild.lineTo(childx2, childy2);
    	
		 pathMeasureParent = new PathMeasure(animPathParent, false);
	     pathLengthParent = pathMeasureParent.getLength();
	     
	     pathMeasureChild = new PathMeasure(animPathChild, false);
	     pathLengthChild = pathMeasureChild.getLength();
        
	    
	        canvas.drawPath(animPathParent, paint2);
	        canvas.drawPath(animPathChild, paint2);
	    


        if(distance < pathLengthParent){

            pathMeasureParent.getPosTan(distance, pos, tan);

            matrix.reset();
            //float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees = 0;
            matrix.postRotate(degrees, bmparent_offsetX, bmparent_offsetY);

           
            	matrix.postTranslate(pos[0]-bmparent_offsetX, pos[1]-bmparent_offsetY);
        
            canvas.drawBitmap(bm, matrix, null);

            distance += step;


        }else{
            //distance = 0;
            matrix.reset();
           // float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees=0;
            matrix.postRotate(degrees, bmparent_offsetX, bmparent_offsetY);
            
            matrix.postTranslate(pos[0]-bmparent_offsetX, pos[1]-bmparent_offsetY);
            

            canvas.drawBitmap(bm, matrix, null);

        }
        
        
        // ANIMATING AVATAR  FOR CHILD
        

        if(distanceChild < pathLengthChild){

            pathMeasureChild.getPosTan(distanceChild, posChild, tanChild);

            matrixChild.reset();
            //float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees = 0;
           //Log.i("Dheera - ", "Inside AnimationView distance < pathLength...degrees= " + degrees);
            matrixChild.postRotate(degrees, bmchild_offsetX, bmchild_offsetY);

            
            	matrixChild.postTranslate(posChild[0]-bmchild_offsetX, posChild[1]-bmchild_offsetY);
            

            canvas.drawBitmap(bmChild, matrixChild, null);

            distanceChild += stepChild;
           

        }else{
            //distance = 0;
            matrixChild.reset();
           // float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees=0;
            matrixChild.postRotate(degrees, bmchild_offsetX, bmchild_offsetY);

           
            	matrixChild.postTranslate(posChild[0]-bmchild_offsetX, posChild[1]-bmchild_offsetY);
            

            canvas.drawBitmap(bmChild, matrixChild, null);
           
        }
        invalidate();
    }

}