//
//  UIButtonCard.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/3/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class UIButtonCard: UIButton {
    
    var cardSelected: Bool = false
    
    // this is whether card is playable in context of game round
    var selectable: Bool = false
    
    // this is whether card is playable in context of state for player in turn
    var selectableTintEnabled: Bool = false
    
    var originalPosition: CGPoint?
    
    var card: String?
    
    var tintView: UIView?
    
//    func isSelectable() -> Bool {
//        return selectable && selectableOverride
//    }
    
    func createOverlay(width: CGFloat, height: CGFloat) {
        tintView = UIView()
        tintView!.backgroundColor = UIColor(white: 0, alpha: 0.35)
        tintView!.frame = CGRect(x: 0, y: 0, width: width, height: height)
    }
    
    func setOverlay() {
        if selectableTintEnabled && !selectable {
            addSubview(tintView!)
        } else {
            tintView!.removeFromSuperview()
        }
    }
    
    func getText() -> String {
        var txt = ""
        if card != nil {
            switch String(card!.dropLast()) {
            case "A":
                txt += "Ace"
            case "K":
                txt += "King"
            case "Q":
                txt += "Queen"
            case "J":
                txt += "Jack"
            default:
                txt += String(card!.dropLast())
            }
            switch card!.suffix(1) {
            case "S":
                txt += " of spades"
            case "H":
                txt += " of hearts"
            case "C":
                txt += " of clubs"
            case "D":
                txt += " of diamonds"
            default:
                txt += " of I don't know"
            }
            
        }
        return txt
    }
    
}
