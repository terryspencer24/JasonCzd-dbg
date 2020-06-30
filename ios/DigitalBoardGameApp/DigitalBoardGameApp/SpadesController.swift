//
//  SpadesController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/2/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit
import AudioToolbox

class SpadesController: GenericSocketController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    var user = UserService()
    
    var api = ApiService()
    
    private var currentState: String?
    
    private var cards = Array<Dictionary<String, Any>>()
    
    //private var buttonCards = [UIButtonCard]()
    
    private var buttonCards2 = [UIButtonCard]()
    
    @IBOutlet weak var bidPicker: UIPickerView!
    
    @IBOutlet weak var connectedLabel: UILabel!
    
    var bids = [String]()
    
    @IBOutlet weak var bidButton: UIButton!
    
    @IBOutlet weak var readyButton: UIButton!
    
    override func viewDidLoad() {
        initPicker()
        modeWait()
        super.viewDidLoad()
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.requestStatus()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        AppDelegate.AppUtility.lockOrientation(.landscapeLeft)
        UIDevice.current.setValue(UIInterfaceOrientation.landscapeLeft.rawValue, forKey: "orientation")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        AppDelegate.AppUtility.clearLock()
        UIDevice.current.setValue(UIInterfaceOrientation.portrait.rawValue, forKey: "orientation")
    }
    
    @IBAction func homeButtonClicked(_ sender: Any) {
        self.navigationController?.popToRootViewController(animated: true)
    }
    
    func initPicker() {
        bids = [ "Nil", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" ]
        self.bidPicker.delegate = self
        self.bidPicker.dataSource = self
        self.bidPicker.selectRow(3, inComponent: 0, animated: true)
    }
    
    override func getDestReceive() -> String {
        return "/out/game/\(gameid())/player/\(self.user.id()!)"
    }
    
    override func getDestSend() -> String {
        return "/inb/game/\(gameid())/player/\(user.id()!)"
    }
    
    override func getMsgHandler() -> (Dictionary<String, Any>) -> Void {
        return {(json) in
            let typex = json["type"] as! String
//            print("Message received: \(typex)")
            if typex == "state" {
                let statex = json["message"] as! String
                if statex == "ready" {
                    self.modeReady()
                } else if statex == "bid" {
                    self.modeBid()
                } else if statex == "play" {
                    self.modePlay()
                } else if statex == "wait" {
                    self.modeWait()
                }
            } else if typex == "hand" {
                let cardz = json["message"] as! Array<Dictionary<String, Any>>
                self.cards = cardz
                self.loadCards()
            }
        }
    }
    
    override func getErrHandler() -> (Bool) -> Void {
        return {(connected) in
            self.connectedLabel.isHidden = connected
        }
    }
    
    func requestStatus() {
        send(obj: [
            "type" : "status"
        ])
    }
    
    func sendReady() {
        send(obj: [
            "type" : "ready"
        ])
    }
    
    func sendBid(bid: Int) {
        send(obj: [
            "type" : "bid",
            "message" : bid
        ])
    }
    
    func sendPlay(card: String) {
        send(obj: [
            "type" : "play",
            "message" : card
        ])
    }
    
    let cardContainer: UIView = UIView()
    let containerTopBuffer = CGFloat(50)
    func loadCards() {
        print("loadCards()")
//        for buttonCard in buttonCards {
//            buttonCard.removeFromSuperview()
//        }
//        buttonCards.removeAll()
//        for i in 0..<self.cards.count {
//            let buttonCard = createCard(img: self.cards[i]["name"] as! String, sel: self.cards[i]["valid"] as! Int == 1, x: i*20+40, y: 600)
//            buttonCards.append(buttonCard)
//        }
        
        
        
        // set up container
        for i in 0..<self.buttonCards2.count {
            self.buttonCards2[i].removeFromSuperview()
        }
        self.cardContainer.removeFromSuperview()
        self.cardContainer.removeConstraints(self.cardContainer.constraints)
        cardContainer.translatesAutoresizingMaskIntoConstraints = false
//        cardContainer.backgroundColor = UIColor.red
        self.view.addSubview(cardContainer)
        let cardWidth = CGFloat(self.getX(x: 40))
        let cardHeight = 1.45 * CGFloat(cardWidth)
        let cardSpacer = CGFloat(cardWidth * 0.4)
        let containerWidth = (CGFloat(self.cards.count - 1) * cardSpacer) + cardWidth
//        print("cardwidth=\(cardWidth) cardheight=\(cardHeight) cardspacer=\(cardSpacer) containerwidth=\(containerWidth) cardcount=\(self.cards.count)")
        cardContainer.widthAnchor.constraint(equalToConstant: containerWidth).isActive = true
        cardContainer.heightAnchor.constraint(equalToConstant: cardHeight + (containerTopBuffer)).isActive = true
        cardContainer.centerXAnchor.constraint(equalTo: self.view.centerXAnchor).isActive = true
        cardContainer.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: CGFloat(self.getY(y: -25))).isActive = true
        
        // set up cards
        buttonCards2.removeAll()
        for i in 0..<self.cards.count {
            let img = self.cards[i]["name"] as! String
            let cardButton = tempCard(cardContainer: cardContainer, img: img, sel: self.cards[i]["valid"] as! Int == 1, top: containerTopBuffer, left: (cardSpacer * CGFloat(i)), width: cardWidth, height: cardHeight)
            buttonCards2.append(cardButton)
        }
    }
    
    func tempCard(cardContainer: UIView, img: String, sel: Bool, top: CGFloat, left: CGFloat, width: CGFloat, height: CGFloat) -> UIButtonCard {
        let cardButton: UIButtonCard = UIButtonCard()
        cardButton.card = img
        
        cardButton.setImage(UIImage(named: img, in: Bundle(for: type(of: self)), compatibleWith: self.traitCollection), for: .normal)
        cardButton.translatesAutoresizingMaskIntoConstraints = false
        cardContainer.addSubview(cardButton)
        let widthConstraint2 = NSLayoutConstraint(item: cardButton, attribute: .width, relatedBy: .equal,
                                                 toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: width)
        let heightConstraint2 = NSLayoutConstraint(item: cardButton, attribute: .height, relatedBy: .equal,
                                                  toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: height)
        let xConstraint2 = NSLayoutConstraint(item: cardButton, attribute: .left, relatedBy: .equal, toItem: cardContainer, attribute: .left, multiplier: 1, constant: left)
        let yConstraint2 = NSLayoutConstraint(item: cardButton, attribute: .bottom, relatedBy: .equal, toItem: cardContainer, attribute: .bottom, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([widthConstraint2, heightConstraint2, xConstraint2, yConstraint2])
        
        cardButton.selectable = sel
        cardButton.selectableTintEnabled = currentState=="play"
        cardButton.createOverlay(width: width, height: height)
        cardButton.setOverlay()
        cardButton.originalPosition = CGPoint(x: left, y: top)
        
        let swipeUp = UISwipeGestureRecognizer(target: self, action: #selector(self.respondToSwipeGesture2(gesture:)))
        swipeUp.direction = UISwipeGestureRecognizer.Direction.up
        cardButton.addGestureRecognizer(swipeUp)
        
        cardButton.addTarget(self, action: #selector(SpadesController.cardClicked2(button:)), for: .touchUpInside)
        return cardButton
    }
    
    @objc func respondToSwipeGesture2(gesture: UIGestureRecognizer) {
        let button = gesture.view as! UIButtonCard
        fireCardPlay(button: button)
    }
    
    func fireCardPlay(button: UIButtonCard) {
        animate(button: button, dest: CGPoint(x: button.originalPosition!.x, y: -500), cb: {(tf) in
            self.sendPlay(card: button.card!)
        })
    }

    @objc func cardClicked2(button: UIButtonCard) {
        if (currentState=="play" && button.selectable) {
            if !button.cardSelected {
                for button2 in buttonCards2 {
                    if button2.cardSelected {
                        animate(button: button2, dest: CGPoint(x: button2.originalPosition!.x, y: button2.originalPosition!.y))
                        button2.cardSelected = false
                    }
                }
                animate(button: button, dest: CGPoint(x: button.originalPosition!.x, y: 0))
                button.cardSelected = true
            } else {
                fireCardPlay(button: button)
            }
//            if (button.cardSelected) {
//                animate(button:button, dest:button.originalPosition!)
//                button.cardSelected = false
//                buttonPlay.isHidden = true
//            } else {
//                for button2 in buttonCards {
//                    if button2.cardSelected {
//                        cardClicked(button:button2)
//                    }
//                }
//                animate(button:button, dest:CGPoint(x: self.getX(x: 160), y: self.getY(y: 320)))
//                button.cardSelected = true
//                buttonPlay.isHidden = false
//            }
        }
    }
    
    func createCard(img: String, sel: Bool, x: Int, y: Int) -> UIButtonCard {
        let bundle = Bundle(for: type(of: self))
        let card = UIImage(named: img, in: bundle, compatibleWith: self.traitCollection)
        let button = UIButtonCard()
        button.setImage(card, for: .normal)
        button.frame = CGRect(x: x, y: y, width: 100, height: 145)
        button.center = CGPoint(x: self.getX(x:x), y: self.getY(y:y))
        button.originalPosition = button.center
        button.addTarget(self, action: #selector(SpadesController.cardClicked(button:)), for: .touchUpInside)
        
        let swipeUp = UISwipeGestureRecognizer(target: self, action: #selector(self.respondToSwipeGesture(gesture:)))
        swipeUp.direction = UISwipeGestureRecognizer.Direction.up
        button.addGestureRecognizer(swipeUp)
        
        button.card = img
        button.selectable = sel
        button.selectableTintEnabled = currentState=="play"
        //button.createOverlay()
        button.setOverlay()
        self.view.addSubview(button)
        return button
    }
    
    @objc func respondToSwipeGesture(gesture: UIGestureRecognizer) {
        cardClicked(button: gesture.view as! UIButtonCard)
    }

    @objc func cardClicked(button: UIButtonCard) {
        if (currentState=="play" && button.selectable) {
            if (button.cardSelected) {
                animate(button:button, dest:button.originalPosition!)
                button.cardSelected = false
            } else {
//                for button2 in buttonCards {
//                    if button2.cardSelected {
//                        cardClicked(button:button2)
//                    }
//                }
                animate(button:button, dest:CGPoint(x: self.getX(x: 160), y: self.getY(y: 320)))
                button.cardSelected = true
            }
        }
    }
    
    func animate(button: UIButtonCard, dest: CGPoint) {
        animate(button: button, dest: dest, cb: {(tf) in })
    }
    
    func animate(button: UIButtonCard, dest: CGPoint, cb: @escaping (Bool) -> Void) {
        UIView.animate(withDuration: 0.30, delay: 0, options: .curveLinear, animations: {
            button.frame.origin = dest
        }, completion: cb)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
 
    // Number of columns of data
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    // The number of rows of data
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return bids.count
    }
    
    // The data to return for the row and component (column) that's being passed in
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return bids[row]
    }
    
    @IBAction func bidButtonClicked(_ sender: Any) {
        sendBid(bid: bidPicker.selectedRow(inComponent: 0))
    }
    
    @IBAction func readyButtonClicked(_ sender: Any) {
        sendReady()
    }
    
    func isModePlay() -> Bool {
        return self.currentState != nil && self.currentState == "play"
    }
    
    func modeBid() {
        print("modeBid()")
        self.currentState = "bid"
//        for button in buttonCards {
//            button.selectableTintEnabled = isModePlay()
//            button.setOverlay()
//        }
        
        for button in buttonCards2 {
            button.selectableTintEnabled = isModePlay()
            button.setOverlay()
        }
        
        // set up bidPicker
        
        bidPicker.isHidden = false
        bidPicker.translatesAutoresizingMaskIntoConstraints = false
        //bidPicker.backgroundColor = UIColor.blue
        let widthConstraint = NSLayoutConstraint(item: bidPicker!, attribute: .width, relatedBy: .equal,
                                                 toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: CGFloat(self.getX(x: 75)))
        let heightConstraint = NSLayoutConstraint(item: bidPicker!, attribute: .height, relatedBy: .equal,
                                                  toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: CGFloat(self.getY(y: 150)))
        bidPicker.centerXAnchor.constraint(equalTo: self.view.centerXAnchor).isActive = true
        bidPicker.topAnchor.constraint(equalTo: self.view.topAnchor, constant: CGFloat(self.getY(y: 75))).isActive = true
        NSLayoutConstraint.activate([widthConstraint, heightConstraint])

        
        // TODO bid button needs to be centered and adjusted vertically.. click doesn't work when it's covered by card frame
        
        // set up bidButton
        bidButton.translatesAutoresizingMaskIntoConstraints = false
        bidButton.centerXAnchor.constraint(equalTo: self.view.centerXAnchor).isActive = true
        bidButton.topAnchor.constraint(equalTo: bidPicker.bottomAnchor, constant: CGFloat(self.getY(y: 5))).isActive = true
        bidButton.isHidden = false
        readyButton.isHidden = true
        
        
    }
    
    func modePlay() {
        print("modePlay()")
        self.currentState = "play"
//        for button in buttonCards {
//            button.selectableTintEnabled = isModePlay()
//            button.setOverlay()
//        }
        for button in buttonCards2 {
            button.selectableTintEnabled = isModePlay()
            button.setOverlay()
        }
        bidPicker.isHidden = true
        bidButton.isHidden = true
        readyButton.isHidden = true
    }
    
    func modeReady() {
        print("modeReady()")
        self.currentState = "ready"
        if self.currentState == nil || self.currentState! == "ready" {
//            for button in buttonCards {
//                button.selectableTintEnabled = isModePlay()
//                button.setOverlay()
//            }
            for button in buttonCards2 {
                button.selectableTintEnabled = isModePlay()
                button.setOverlay()
            }
            bidPicker.isHidden = true
            bidButton.isHidden = true
            readyButton.isHidden = false
            AudioServicesPlayAlertSound(SystemSoundID(kSystemSoundID_Vibrate))
        }
    }
    
    func modeWait() {
        print("modeWait()")
        self.currentState = "wait"
//        for button in buttonCards {
//            button.selectableTintEnabled = isModePlay()
//            button.setOverlay()
//        }
        for button in buttonCards2 {
            button.selectableTintEnabled = isModePlay()
            button.setOverlay()
        }
        bidPicker.isHidden = true
        bidButton.isHidden = true
        readyButton.isHidden = true
    }
    
}
