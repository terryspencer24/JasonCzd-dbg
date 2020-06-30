//
//  ViewController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/29/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    let data = DataService()
    
    let user = UserService()
    
    @IBOutlet weak var loginButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        UIUtility.setupButton(button: loginButton)
    }
    
    func initialize() {
        if (data.hasValidToken()) {
            user.load(cb: {(res, json) in
                if res.statusCode == 200 {
                    DispatchQueue.main.async {
                        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                        let modeSelectController = storyBoard.instantiateViewController(withIdentifier: "modeselectnav")
                        self.present(modeSelectController, animated: true, completion: nil)
                    }
                } else if (res.statusCode == 401){
                    self.notAuthenticated()
                } else {
                    UIUtility.displayAlert(title: "Error", message: "Server error encountered (\(res.statusCode)).", btn: "Retry", uivc: self, cb: {(str) in
                        if str == "default" {
                            self.initialize()
                        }
                    })
                }
            }, ecb: {(data, response, error) in
                UIUtility.displayAlert(title: "Error", message: "Unable to communicate with server.", btn: "Retry", uivc: self, cb: {(str) in
                    if str == "default" {
                        self.initialize()
                    }
                })
            })
        } else {
            notAuthenticated()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        initialize()
    }
    
    func notAuthenticated() {
        DispatchQueue.main.async {
            self.loginButton.isHidden = false
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let loginController = segue.destination as? LoginController
            else {
                return
        }
        loginController.callbackClosure = { [weak self] in
            self?.initialize()
        }
        loginButton.isHidden = true
    }
    
}

