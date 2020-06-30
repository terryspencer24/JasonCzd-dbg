//
//  LoginController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/29/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class LoginController: UIViewController {
    
    @IBOutlet weak var username: UITextField!
    
    @IBOutlet weak var password: UITextField!
    
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    
    @IBOutlet weak var cancelButton: UIButton!
    
    let api = ApiService()
    
    let data = DataService()
    
    var callbackClosure: (() -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction func loginButtonClicked(_ sender: Any) {
        login(user: self.username.text!, pass: self.password.text!)
    }

    @IBAction func cancelButtonClicked(_ sender: Any) {
        DispatchQueue.main.async {
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    func login(user: String, pass: String) {
        let parms = [
            "username": user,
            "password": pass
        ]
        self.spinner.startAnimating()
        api.getauth(url: "\(Prop.URL)/papi/users/login", method: "POST", token: nil, obj: parms, cb: {(res, json) in
            DispatchQueue.main.async {
                self.spinner.stopAnimating()
            }
            if (res.statusCode == 200) {
                if (json.count == 1) {
                    let token: String = json[0]["value"] as! String
                    self.data.setAuthToken(token: token)
                    DispatchQueue.main.async {
                        self.dismiss(animated: true, completion: nil)
                    }
                } else {
                    UIUtility.displayAlert(title: "Error", message: "Unexpected response from server.", btn: "Close", uivc: self, cb: {(str) in
                    })
                }
            } else {
                if res.statusCode == 401 {
                    UIUtility.displayAlert(title: "Login Failure", message: "User and password combination is invalid.", btn: "Close", uivc: self, cb: {(str) in
                    })
                } else {
                    UIUtility.displayAlert(title: "Error", message: "Server error encountered (\(res.statusCode)).", btn: "Close", uivc: self, cb: {(str) in
                    })
                }
            }
        }, ecb: {(data, response, error) in
            UIUtility.displayAlert(title: "Error", message: "Unable to communicate with server.", btn: "Close", uivc: self, cb: {(str) in
            })
        })
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if self.callbackClosure != nil {
            self.callbackClosure!()
        }
    }
    
}
