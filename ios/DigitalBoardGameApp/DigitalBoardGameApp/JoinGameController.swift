//
//  JoinGameController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/31/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class JoinGameController: UITableViewController {

    let api = ApiService()
    
    let data = DataService()
    
    var games = [Dictionary<String, Any>]()
    
    var dateSections = [Date]()
    
    var gamesInSection = [[Dictionary<String, Any>]]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 300
        
        self.loadGames()
    }
    
    func loadGames() {
        self.games = [Dictionary<String, Any>]()
        api.getauth(url: "\(Prop.URL)/api/tickets?sort=newest", method: "GET", token: data.getAuthToken()!, obj: nil, cb:
            {(res, json) in
                self.games += json
                self.initSections()
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }, ecb: {(data, response, error) in
                UIUtility.displayAlert(title: "Error", message: "Unable to retrieve games.", btn: "Close", uivc: self, cb: {(str) in
                })
            }
        )
    }
    
    func initSections() {
        var prevDt: Date?
        if (self.games.count > 0) {
            for i in 1...self.games.count {
                let curDt = UIUtility.getDateFromString(isoDate: self.games[i-1]["startdate"] as! String)
                if (self.games[i-1]["parsedDt"] == nil) {
                    self.games[i-1]["parsedDt"] = curDt
                }
                if prevDt == nil || Calendar.current.compare(curDt, to: prevDt!, toGranularity: .day) != .orderedSame {
                    self.dateSections.append(curDt)
                    self.gamesInSection.append([Dictionary<String, Any>]())
                    prevDt = curDt
                }
                self.gamesInSection[self.gamesInSection.count-1].append(self.games[i-1])
            }
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return self.dateSections.count
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .medium
        dateFormatter.timeStyle = .none
        return dateFormatter.string(from: self.dateSections[section])
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return gamesInSection[section].count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let game:Dictionary<String, Any> = self.gamesInSection[indexPath.section][indexPath.row]
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "JoinGameCell", for: indexPath) as? JoinGameTableViewCell else {
            fatalError("The dequeued cell is not an instance")
        }
        
        var text:String = (game["gameName"] as! String)
        
        // id
        text += " ("
        text += String(game["id"] as! Int)
        text += ")"
        
        // users
        var users = ""
        let invites = (game["invites"] as! NSArray)
        for invite in invites {
            let invitedict = invite as! Dictionary<String, Any>
            let username = invitedict["username"] as! String
            if username != "bot" {
                users += username
                users += ", "
            }
        }
        cell.gameInvites!.text = String(users.dropLast(2))
        
        cell.cellText!.text = text
        return cell
    }

    @IBAction func backButtonClicked(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let gameLobbyController = segue.destination as? GameLobbyController,
            let row = tableView.indexPathForSelectedRow?.row, let section = tableView.indexPathForSelectedRow?.section
            else {
                return
        }
        gameLobbyController.game = gamesInSection[section][row]
    }
    
}
