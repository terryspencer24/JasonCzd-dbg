//
//  CreateGameController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/4/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit
import MobileCoreServices

class CreateGameController: UIViewController, UITableViewDragDelegate, UITableViewDropDelegate, UITableViewDelegate,  UITableViewDataSource {
    
    let api = ApiService()
    
    let data = DataService()
    
    let user = UserService()

    var game: Dictionary<String, Any> = Dictionary<String, Any>()
    
    var invites: [Dictionary<String, Any>] = [Dictionary<String, Any>]()
   
    @IBOutlet weak var invitesTableView: UITableView!
    
    var seats: [String] = [ "Seat 1", "Seat 2", "Seat 3", "Seat 4" ]
    
    @IBOutlet weak var seatsTableView: UITableView!
    
    var name: String?
    
    @IBOutlet weak var labelName: UILabel!
    
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    
    @IBOutlet weak var createButton: UIButton!
    
    var bot = Dictionary<String, Any>()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        UIUtility.setupButton(button: createButton)
        
        name = game["name"] as? String
        labelName.text = name
        initSeats()
        initInvites()
    }
    
    
    
    @IBAction func clickCreateButton(_ sender: Any) {
        
        let jsonObj:Dictionary<String, Any> = [
//            :
            "gameName" : self.name!,
//            "cartDetails" : [
//                "customerID" : "sathish",
//                "cartAmount" : "6999",
//                "cartShipping" : "1",
//                "cartTax1" : "69",
//                "cartTax2" : "",
//                "cartTax3" : "",
//                "cartCouponCode" : "",
//                "cartCouponAmount" : "",
//                "cartPaymentMethod" : "",
//                "cartProductItems" : [
//                    "productID" : "9",
//                    "productPrice" : "6999",
//                    "productQuantity" : "1"
//                ]
//            ]
        ]
        
        self.spinner.startAnimating()
        
        self.api.getauth(url: "\(Prop.URL)/api/tickets", method: "POST", token: self.data.getAuthToken()!, obj: jsonObj, cb: {(res, json2) in
            var game = json2
            game[0]["invites"] = self.invites
            game[0]["started"] = 1
            
            self.api.getauth(url: "\(Prop.URL)/api/tickets", method: "PUT", token: self.data.getAuthToken()!, obj: game[0], cb: {(res, json3) in
                self.stopSpinner()
                DispatchQueue.main.async {
                    let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                    let gamelobbyController = storyBoard.instantiateViewController(withIdentifier: "gamelobby") as! GameLobbyController
                    gamelobbyController.game = json3[0]
                    self.navigationController?.pushViewController(gamelobbyController, animated: true)
                }
            }, ecb: {(data, response, error) in
                self.stopSpinner()
                UIUtility.displayAlert(title: "Error", message: "Unable to update and start game.", btn: "Close", uivc: self, cb: {(str) in
                })
            })
        }, ecb: {(data, response, error) in
            self.stopSpinner()
            UIUtility.displayAlert(title: "Error", message: "Unable to create game.", btn: "Close", uivc: self, cb: {(str) in
            })
        })
    }
    
    func stopSpinner() {
        DispatchQueue.main.async {
            self.spinner.stopAnimating()
        }
    }
    
    
    // MARK: - seats table view
    
    func initSeats() {
        seatsTableView.register(UITableViewCell.self, forCellReuseIdentifier: "cellId")
        seatsTableView.delegate = self
        seatsTableView.dataSource = self
    }
    
    // MARK: - invites table view
    
    func initInvites() {
        invites.append([
            "id" : self.user.id()!,
            "username" : self.user.username()!
        ])
        self.spinner.startAnimating()
        api.getauth(url: "\(Prop.URL)/api/users?q=bot", method: "GET", token: data.getAuthToken()!, obj: nil, cb: {(res, json) in
            self.bot = json[0]
            self.invites.append(self.bot)
            self.invites.append(self.bot)
            self.invites.append(self.bot)
            DispatchQueue.main.async {
                self.stopSpinner()
                //self.invitesTableView.register(UITableViewCell.self, forCellReuseIdentifier: "cellId")
//                self.invitesTableView.register(CreateGameTableViewCell.self, forCellReuseIdentifier: "CreateGameCell")
                self.invitesTableView.delegate = self
                self.invitesTableView.dataSource = self
                self.invitesTableView.dragInteractionEnabled = true
                self.invitesTableView.isScrollEnabled = false
                self.initInvitesDragDrop()
                self.invitesTableView.reloadData()
            }
        }, ecb: {(data, response, error) in
            self.stopSpinner()
            UIUtility.displayAlert(title: "Error", message: "Unable to initialize user list.", btn: "Close", uivc: self, cb: {(str) in
                // TODO what to do here?
            })
        })
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == self.seatsTableView {
            return seats.count
        }
        return invites.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == seatsTableView {
            let cell = seatsTableView.dequeueReusableCell(withIdentifier: "cellId", for: indexPath)
            cell.backgroundColor = UIColor.white
            cell.textLabel?.text = seats[indexPath.row]
            return cell
        }
        
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "CreateGameCell", for: indexPath) as? CreateGameTableViewCell else {
            fatalError("The dequeued cell is not an instance")
        }
        
        let username: String = invites[indexPath.row]["username"] as! String
        cell.pencilImage.isHidden = user.username() == username
        cell.inviteeName.text = invites[indexPath.row]["username"] as? String
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if tableView == self.seatsTableView {
            return
        }
        
        if (self.invites[indexPath.row]["id"] as! Int) == self.user.id() as! Int {
            invitesTableView.deselectRow(at: indexPath, animated: true)
        } else {
            let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
            let searchUserController = storyBoard.instantiateViewController(withIdentifier: "searchuser") as! SearchUserController
            for invite in self.invites {
                if invite["id"] as! Int != self.bot["id"] as! Int {
                    searchUserController.alreadySelectedUsers.append(invite)
                }
            }
            searchUserController.callback = {(user) in
                if (self.invitesTableView.indexPathForSelectedRow != nil) {
                    let indexPath = self.invitesTableView.indexPathForSelectedRow
                    if indexPath != nil {
                        if (user != nil) {
                            DispatchQueue.main.async {
                                self.invites[indexPath!.row] = user!
                                self.invitesTableView.reloadData()
                            }
                        } else {
                            self.invitesTableView.deselectRow(at: indexPath!, animated: true)
                        }
                    }
                }
            }
            self.present(searchUserController, animated: true, completion: nil)
        }
    }
    
    
    // MARK: - invites table drag/drop
    
    func initInvitesDragDrop() {
        invitesTableView.dragDelegate = self
        invitesTableView.dropDelegate = self
    }
    
    func tableView(_ tableView: UITableView, itemsForBeginning session: UIDragSession, at indexPath: IndexPath) -> [UIDragItem] {
        print("itemsForBeginning")
        return dragItems(for: indexPath)
    }
    
    // this method starts drag...
    func dragItems(for indexPath: IndexPath) -> [UIDragItem] {
        let invite = invites[indexPath.row]

        let data = (invite["username"] as! String).data(using: .utf8)
        let itemProvider = NSItemProvider()
        
        itemProvider.registerDataRepresentation(forTypeIdentifier: kUTTypePlainText as String, visibility: .all) { completion in
            completion(data, nil)
            return nil
        }

        return [
            UIDragItem(itemProvider: itemProvider)
        ]
    }
    
    func tableView(_ tableView: UITableView, canHandle session: UIDropSession) -> Bool {
        return canHandle(session)
    }
    
    func canHandle(_ session: UIDropSession) -> Bool {
        return session.canLoadObjects(ofClass: NSString.self)
    }
    
    func tableView(_ tableView: UITableView, dropSessionDidUpdate session: UIDropSession, withDestinationIndexPath destinationIndexPath: IndexPath?) -> UITableViewDropProposal {
        // The .move operation is available only for dragging within a single app.
        if tableView.hasActiveDrag {
            if session.items.count > 1 {
                return UITableViewDropProposal(operation: .cancel)
            } else {
                return UITableViewDropProposal(operation: .move, intent: .insertAtDestinationIndexPath)
            }
        } else {
            return UITableViewDropProposal(operation: .copy, intent: .insertAtDestinationIndexPath)
        }
    }
    
    // this method finishes drop
    func tableView(_ tableView: UITableView, performDropWith coordinator: UITableViewDropCoordinator) {
        let destinationIndexPath: IndexPath
        
        if let indexPath = coordinator.destinationIndexPath {
            destinationIndexPath = indexPath
        } else {
            // Get last index path of table view.
            let section = tableView.numberOfSections - 1
            let row = tableView.numberOfRows(inSection: section)
            destinationIndexPath = IndexPath(row: row, section: section)
        }
        
        coordinator.session.loadObjects(ofClass: NSString.self) { items in
            // Consume drag items.
            let stringItems = items as! [String]
            
            var indexPaths = [IndexPath]()
            for (index, item) in stringItems.enumerated() {
                let indexPath = IndexPath(row: destinationIndexPath.row + index, section: destinationIndexPath.section)
                
                var oldidx = 0
                for (idx, invite) in self.invites.enumerated() {
                    if item == invite["username"] as! String {
                        oldidx = idx
                        break
                    }
                }
                let obj = self.invites[oldidx];
                self.invites.remove(at: oldidx)
                self.invites.insert(obj, at: indexPath.row)
                indexPaths.append(indexPath)
                print("dropping \(item) to \(indexPath.row)")
                tableView.reloadData()
                print(self.invites.count)
                // todo update invites obj
            }

            //tableView.insertRows(at: indexPaths, with: .automatic)
        }
    }
    
}
