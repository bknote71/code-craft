document.addEventListener("DOMContentLoaded", function () {
    const audioPlayer = document.getElementById('audioPlayer');
    const muteUnmuteBtn = document.getElementById('muteUnmuteBtn');
    const volumeControl = document.getElementById('volumeControl');
    const muteUnmuteIcon = document.getElementById('muteUnmuteIcon'); // get the icon inside the button

    audioPlayer.volume = volumeControl.value;

    // Mute or unmute the audio
    muteUnmuteBtn.addEventListener('click', function () {
        if (audioPlayer.muted) {
            audioPlayer.muted = false;
            muteUnmuteIcon.src = "assets/volume.png"; // Set to volume icon
        } else {
            audioPlayer.muted = true;
            muteUnmuteIcon.src = "assets/mute.png"; // Set to mute icon
        }
    });
    
    // Adjust the volume
    volumeControl.addEventListener('input', function () {
        audioPlayer.volume = volumeControl.value;
    });
});
