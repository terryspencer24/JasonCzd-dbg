//
//  SearchUserController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/11/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class SearchUserController: UIViewController, UITableViewDelegate,  UITableViewDataSource {

    let api = ApiService()
    
    let data = DataService()
    
    let debouncer = Debouncer(timeInterval: 0.5)
    
    var callback : ((Dictionary<String, Any>?) -> Void)?
    
    var alreadySelectedUsers = [Dictionary<String, Any>]()
    
    var alreadySelectedIds = [Int]()
    
    var users = [Dictionary<String, Any>]()
    
    @IBOutlet weak var userView: UITableView!
    
    @IBOutlet weak var userTextField: UITextField!
    
    @IBOutlet weak var cancelButton: UIButton!
    
    @IBAction func textEdit(_ sender: Any) {
        debouncer.handler = {
            if self.userTextField.text!.count >= 2 {
                self.searchUsers(q: self.userTextField.text!)
            }
        }
        debouncer.renewInterval()
    }
    
    override func viewDidLoad() {
        userView.register(UITableViewCell.self, forCellReuseIdentifier: "cellId")
        userView.delegate = self
        userView.dataSource = self
        userTextField.becomeFirstResponder()
        //UIUtility.setupButton(button: cancelButton)
        processSelectedIds()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if (self.userView.indexPathForSelectedRow == nil) {
            self.callback?(nil)
        }
    }
    
    func processSelectedIds() {
        alreadySelectedIds = [Int]()
        for alreadySelectedUser in alreadySelectedUsers {
            let id = alreadySelectedUser["id"] as! Int
            if !alreadySelectedIds.contains(id) {
                alreadySelectedIds.append(id)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return users.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // indexPath.row
        let cell = userView.dequeueReusableCell(withIdentifier: "cellId", for: indexPath)
        cell.backgroundColor = UIColor.white
        cell.textLabel?.text = users[indexPath.row]["username"] as? String
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.callback?(self.users[indexPath.row])
        DispatchQueue.main.async {
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    func searchUsers(q: String) {
        self.users = [Dictionary<String, Any>]()
        api.getauth(url: "\(Prop.URL)/api/users?q=\(q)", method: "GET", token: data.getAuthToken()!, obj: nil, cb: {(res, json) in
                self.users = [Dictionary<String, Any>]()
                for j in json {
                    if !self.alreadySelectedIds.contains(j["id"] as! Int) {
                        self.users.append(j)
                    }
                }
                DispatchQueue.main.async {
                    self.userView.reloadData()
                }
            }, ecb: {(data, response, error) in
                UIUtility.displayAlert(title: "Error", message: "Unable to retrieve users.", btn: "Close", uivc: self, cb: {(str) in
                })
            }
        )
    }
    
    @IBAction func clickCancel(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
}
