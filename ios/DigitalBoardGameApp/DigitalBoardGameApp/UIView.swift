//
//  UIView.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/16/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

extension UIView {
    
    enum GlowEffect: Float {
        case small = 0.4, normal = 2, big = 30
    }

    func doGlowAnimation(withColor color: UIColor, withEffect effect: GlowEffect = .normal) {
        layer.masksToBounds = false
        layer.shadowColor = color.cgColor
        layer.shadowRadius = 0
        layer.shadowOpacity = 1
        layer.shadowOffset = .zero

        let glowAnimation = CABasicAnimation(keyPath: "shadowRadius")
        glowAnimation.fromValue = 0
        glowAnimation.toValue = effect.rawValue
        glowAnimation.beginTime = CACurrentMediaTime()+0.3
        glowAnimation.duration = CFTimeInterval(0.3)
        glowAnimation.fillMode = CAMediaTimingFillMode.removed
        glowAnimation.autoreverses = true
        glowAnimation.isRemovedOnCompletion = true
        layer.add(glowAnimation, forKey: "shadowGlowingAnimation")
    }
    
    func doGlow(withColor color: UIColor, withEffect effect: GlowEffect = .normal) {
        layer.shadowColor = color.cgColor
        layer.shadowOffset = CGSize.zero
        layer.shadowRadius = CGFloat(effect.rawValue)
        layer.shadowOpacity = 0.5
        layer.masksToBounds = false
    }
    
}
