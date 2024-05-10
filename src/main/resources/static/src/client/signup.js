import { downloadAssets } from './assets';
import './css/bootstrap-reboot.css';
import pixiApp from './pixi/app.js';
import './css/signup.css';

const wrapper = document.querySelector('.wrapper');
const loginLink = document.querySelector('.login-link');
const registerLink = document.querySelector('.register-link');

let flag = true;

Promise.all([

    downloadAssets(),
    

]).then(() => {
    pixiApp();

    registerLink.addEventListener('click', function () {
        wrapper.classList.add('active');
    });
    
    loginLink.addEventListener('click', function () {
        wrapper.classList.remove('active');
    });

}).catch(console.error);

