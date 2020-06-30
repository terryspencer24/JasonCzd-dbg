//
//  GenericGameController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/4/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class GenericGameController: UIViewController {
    
    var game:Dictionary<String, Any> = Dictionary<String, Any>()
    
    func gameid() -> Int {
        let gameid = self.game["id"] as! Int
        return gameid
    }
    
    func getX(x: Int) -> Int {
        return Int(Float(x) / 320 * Float(UIScreen.main.bounds.size.width))
    }
    
    func getY(y: Int) -> Int {
        return Int(Float(y) / 640 * Float(UIScreen.main.bounds.size.height))
    }
    
}
