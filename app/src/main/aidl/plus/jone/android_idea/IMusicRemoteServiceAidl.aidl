// IMusicRemoteServiceAidl.aidl
package plus.jone.android_idea;

// Declare any non-default types here with import statements
interface IMusicRemoteServiceAidl {
    void play();
    void pause();
    void toggle (int pos);
    int state();
    void stop();
    void next();
    void prev();
    void volumeUp();
    void volumeDown();
}