//
//  ModeSelectController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/31/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class ModeSelectController: UIViewController {
    
    let user = UserService()
    
    @IBOutlet weak var joinButton: UIButton!
    
    @IBOutlet weak var hostButton: UIButton!
    
    @IBOutlet weak var logoutButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        UIUtility.setupButton(button: joinButton)
        UIUtility.setupButton(button: hostButton)
        UIUtility.setupButton(button: logoutButton)
    }

    @IBAction func logoutButtonClicked(_ sender: Any) {
        user.clear(full: true)
        DispatchQueue.main.async {
            self.dismiss(animated: true, completion: nil)
        }
    }
    
}
