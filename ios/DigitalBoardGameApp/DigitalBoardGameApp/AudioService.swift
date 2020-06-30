//
//  AudioService.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/16/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import Foundation
import AVFoundation

class AudioService {
    
    var audioPlayer: AVAudioPlayer?
    
    func play() {
        let alertSound = URL(fileURLWithPath: Bundle.main.path(forResource: "ding", ofType: "mp3")!)
        if audioPlayer == nil {
            try! AVAudioSession.sharedInstance().setCategory(AVAudioSession.Category.playback)
            try! AVAudioSession.sharedInstance().setActive(true)
            try! audioPlayer = AVAudioPlayer(contentsOf: alertSound)
        }
        audioPlayer!.prepareToPlay()
        audioPlayer!.play()
    }
    
    func speak(msg: String) {
        let utterance = AVSpeechUtterance(string: msg)
        utterance.voice = AVSpeechSynthesisVoice(language: "en-US")
        //utterance.rate = .3

        let synthesizer = AVSpeechSynthesizer()
        synthesizer.speak(utterance)
    }
    
}
