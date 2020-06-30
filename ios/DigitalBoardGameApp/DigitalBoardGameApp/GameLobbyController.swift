//
//  GameLobbyController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/2/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class GameLobbyController: UIViewController, UITableViewDelegate,  UITableViewDataSource {
    
    var game:Dictionary<String, Any> = Dictionary<String, Any>()
    
    var invites: [Dictionary<String, Any>] = [Dictionary<String, Any>]()
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var deleteme: UILabel!
    
    @IBOutlet weak var createBoardButton: UIButton!
    
    @IBOutlet weak var viewBoardButton: UIButton!
    
    @IBOutlet weak var invitesView: UITableView!
    
    override func viewDidLoad() {
        initText()
        initInvites()
        initButtons()
    }
    
    func initText() {
        nameLabel.text = "\(game["gameName"] as! String) (\(game["id"] as! Int))"
        
        let dt = UIUtility.getDateFromString(isoDate: game["startdate"] as! String)
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .medium
        dateFormatter.timeStyle = .short
        deleteme.text = dateFormatter.string(from: dt)
    }
    
    func initInvites() {
        self.invites = game["invites"] as! [Dictionary<String, Any>]
        invitesView.register(UITableViewCell.self, forCellReuseIdentifier: "cellId")
        invitesView.delegate = self
        invitesView.dataSource = self
        DispatchQueue.main.async {
            self.invitesView.reloadData()
        }
    }
    
    func initButtons() {
        UIUtility.setupButton(button: createBoardButton)
        UIUtility.setupButton(button: viewBoardButton)
        if UIDevice.current.userInterfaceIdiom == .pad {
            viewBoardButton.isHidden = false
        }
    }
    
    @IBAction func clickEnterButton(_ sender: Any) {
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return invites.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // indexPath.row
        let cell = invitesView.dequeueReusableCell(withIdentifier: "cellId", for: indexPath)
        cell.backgroundColor = UIColor.white
        cell.textLabel?.text = invites[indexPath.row]["username"] as? String
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let gameController = segue.destination as! GenericGameController
        gameController.game = game
    }
    
}
