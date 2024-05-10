function updateEditorBasedOnLanguageSelection() {
    const languageSelectElement = document.getElementById('language-select');

    languageSelectElement.addEventListener('change', function (e) {
        const language = e.target.value;
        let code = '';

        switch (language) {
            case 'java':
                code = `public class FireBot extends Robot {
    @Override
    public void run() {
        while (true) {

        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent){ 

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {

    }

    @Override
    public void onBulletHit(BulletHitEvent bulletHitEvent) {
        
    }

}`;
                break;
            case 'javascript':
                code = `class FireBot {
    run() {
        while (true) {

        }
    }

    onHitByBullet(hitByBulletEvent) {

    }

    onScannedRobot(event) {

    }

    onBulletHit(bulletHitEvent) {

    }
}`;
                break;
            case 'python':
                code = `class FireBot:
    def run(self):
        while True:
            pass
