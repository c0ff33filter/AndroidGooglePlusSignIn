AndroidGooglePlusSignIn
=============
Client-side android play service authentication implementation

In your activity:
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            this.getSupportFragmentManager()
                .beginTransaction()
                .add(new GoogleApiClientSample(),"FragmentTag")
                .commit();
        }
    }
    
    // Let fragment handle onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(resquestCode,resultCode,data);
        GoogleApiClientSample fragment = 
        (GoogleApiClientSample) getSupportFragmentManager()
        .findFragmentByTag("FragmentTag");
        fragment.onActivityResult(resquestCode,resultCode,data);
    }
