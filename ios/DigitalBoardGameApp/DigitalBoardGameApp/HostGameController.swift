//
//  HostGameController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/4/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class HostGameController: UITableViewController {
    
    let api = ApiService()
    
    let data = DataService()
    
    var gameList = [Dictionary<String, Any>]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 300
        
        api.getauth(url: "\(Prop.URL)/api/catalog", method: "GET", token: data.getAuthToken()!, obj: nil, cb: {(res, json) in
            self.gameList += json
            DispatchQueue.main.async {
                self.tableView.reloadData()
            }
        }, ecb: {(data, response, error) in
            UIUtility.displayAlert(title: "Error", message: "Unable to retrieve game list.", btn: "Close", uivc: self, cb: {(str) in
            })
        })
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return gameList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "HostGameCell", for: indexPath) as? HostGameTableViewCell else {
            fatalError("The dequeued cell is not an instance")
        }
        
        cell.nameLabel.text = gameList[indexPath.row]["name"] as? String
        cell.descriptionLabel.text = gameList[indexPath.row]["description"] as? String
        
        return cell
    }
    
//    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
//        return 300;
//    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let createGameController = segue.destination as? CreateGameController,
            let index = tableView.indexPathForSelectedRow?.row
            else {
                return
        }
        createGameController.game = gameList[index]
    }
    
}
