//
//  UIUtility.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/19/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class UIUtility {

    // MARK: - Alert display
    
    static func displayAlert(title: String, message: String, btn: String, uivc: UIViewController, cb: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: btn, style: .default, handler: { action in
                    switch action.style {
                        case .default:
                            cb("default")
                        case .cancel:
                            cb("cancel")
                        case .destructive:
                            cb("destructive")
                        @unknown default: break
                    }
                }
            ))
            uivc.present(alert, animated: true, completion: nil)
        }
    }
    
    static func setupButton(button: UIButton) {
        button.backgroundColor = UIColor.white
        button.layer.cornerRadius = 5
        button.layer.borderWidth = 1
        button.layer.borderColor = UIColor.blue.cgColor
    }
    
    
    // MARK: - Date
    
    static func getDateFromString(isoDate: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        dateFormatter.locale = Locale(identifier: "en_US_POSIX") // set locale to reliable US_POSIX
        let calendar = Calendar.current
        let date = dateFormatter.date(from:isoDate)!
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second, .timeZone], from: date)
        let finalDate = calendar.date(from:components)!
        return finalDate
    }

}

