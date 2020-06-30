//
//  BoardController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/16/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class BoardController: GenericSocketController {
    
    var user = UserService()
    
    var api = ApiService()
    
    var audio = AudioService()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.send(obj: [
                "type" : "board"
            ])
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        AppDelegate.AppUtility.lockOrientation(.portrait)
        UIDevice.current.setValue(UIInterfaceOrientation.portrait.rawValue, forKey: "orientation")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        AppDelegate.AppUtility.clearLock()
        UIDevice.current.setValue(UIInterfaceOrientation.portrait.rawValue, forKey: "orientation")
    }
    
    @IBAction func homeButtonClicked(_ sender: Any) {
        self.navigationController?.popToRootViewController(animated: true)
    }
    
    override func getDestReceive() -> String {
        return "/out/game/\(gameid())/board"
    }
    
    override func getDestSend() -> String {
        return "/inb/game/\(gameid())/board"
    }
    
    var cardzz = [UIButtonCard]()
    var textzz = [UITextView]()
    override func getMsgHandler() -> (Dictionary<String, Any>) -> Void {
        return {(json) in
            
            DispatchQueue.global(qos: .utility).sync{
                
                print("I'm here")
//                let group = DispatchGroup()
//                group.enter()
                
                //let cards: [Dictionary<String, Any>] = json["cards"] as! [Dictionary<String, Any>]
                
                // unwrap cards here as appropriate time and render on display
                //print(type(of: json["message"]["cards"]))
                let start = NSDate().timeIntervalSince1970
                print("started processing update on board \(start)")
                
                let msg = json["message"] as! Dictionary<String, Any>
                print(msg)

                let c2 = msg["cards"] as! Array<String?>
                let trickWinner: Int? = msg["trickWinner"] as? Int
                let animate: Int? = msg["animate"] as? Int
                for cardz in self.cardzz {
                    cardz.removeFromSuperview()
                }
                self.cardzz = [UIButtonCard]()
                
                let xdest = [ 160, 75, 160, 240 ]
                let ydest = [ 530, 340, 160, 340 ]
                
                let xfrom = [ 160, -125, 160, 440 ]
                let yfrom = [ 720, 330, -50, 330 ]
                
                var animated = false
                for (idx, c) in c2.enumerated() {
                    if c != nil {
                        let animating: Bool = animate != nil && animate! == idx
                        let x = animating ? xfrom[idx] : xdest[idx]
                        let y = animating ? yfrom[idx] : ydest[idx]
                        let rotate: Bool = idx == 1 || idx == 3
                        let winner: Bool = trickWinner != nil && trickWinner! == idx
                        let theCard = self.createCard(img: c!, x: x, y: y, rotate: rotate)
                        if animating {
                            animated = true
                            print("before entering main async...")
                            DispatchQueue.main.async {
                                print("getting ready to animate...")
                                UIView.animate(withDuration: 0.30, delay: 0, options: .curveLinear, animations: {
                                    theCard.center = CGPoint(x: self.getX(x: xdest[idx]), y: self.getY(y: ydest[idx]))
                                }, completion: {(bb) in
                                    if winner {
                                        theCard.doGlow(withColor: UIColor.green, withEffect: .big)
                                    }
                                    print("animation should be done, leaving group...")
//                                    group.leave()
                                })
                            }
                        } else {
                            if winner {
                                theCard.doGlow(withColor: UIColor.green, withEffect: .big)
                                self.audio.play()
                            }
                        }
                        self.cardzz.append(theCard)
                    }
                }
                if (!animated) {
//                    group.leave()
                }
                
                // TODO display bids/books
                for textz in self.textzz {
                    textz.removeFromSuperview()
                }
                self.textzz = [UITextView]()
                let bidrot = [ dir.down, dir.left, dir.up, dir.right ]
                let bidx = [ 220, -25, 20, 260 ]
                let bidy = [ 570, 440, 50, 150 ]
                let bidw = [ 200, 200, 200, 200 ]
                let bidh = [ 100, 150, 100, 150 ]
                var txt = [String]()
                if let lastCommands = msg["lastCommands"] as? [Any] {
                    for (idx, c) in lastCommands.enumerated() {
                        if (c is Bool) {
                            let invites = self.game["invites"] as! [Dictionary<String, Any>]
                            var t = invites[idx]["username"] as! String
                            let cc = c as! Bool
                            if !cc {
                                t += " (offline)"
                            }
                            txt.append(t)
                        }
                    }
                }
                if let bids = msg["bids"] as? [Any] {
                    for (idx, b) in bids.enumerated() {
                        if (b is NSDictionary) {
                            let bb = b as! Dictionary<String, Any>
                            txt[idx] += "\r\nBooks: \(bb["won"]!) of \(bb["books"]!)"
                        }
                    }
                }
                for (idx, t) in txt.enumerated() {
                    if t != "" {
                        let text = self.createInf(txt: t, x: bidx[idx], y: bidy[idx], w: bidw[idx], h: bidh[idx], rotate: bidrot[idx])
                        self.textzz.append(text)
                    }
                }
                
                print("ended processing update on board \(start)")
                
//                group.wait()
                
                print("I'm out")
                
            }
            
            
        }
    }
    
    override func getErrHandler() -> (Bool) -> Void {
        return {(connected) in
            //self.connectedLabel.isHidden = connected
        }
    }
    
    func createCard(img: String, x: Int, y: Int, rotate: Bool) -> UIButtonCard {
        let bundle = Bundle(for: type(of: self))
        let card = UIImage(named: img, in: bundle, compatibleWith: self.traitCollection)
        let button = UIButtonCard()
        button.setImage(card, for: .normal)
        button.frame = CGRect(x: x, y: y, width: 200, height: 290)
        button.center = CGPoint(x: self.getX(x:x), y: self.getY(y:y))
        button.originalPosition = button.center
        button.addTarget(self, action: #selector(BoardController.cardClicked(button:)), for: .touchUpInside)
        button.card = img
        button.createOverlay(width: 200, height: 200)
        button.setOverlay()
        if rotate {
            button.transform = CGAffineTransform(rotationAngle: -CGFloat.pi / 2.0)
        }
        self.view.addSubview(button)
        return button
    }

    @objc func cardClicked(button: UIButtonCard) {
        AudioService().speak(msg: button.getText())
    }
    
    enum dir {
        case down, left, up, right
    }
    
    func createInf(txt: String, x: Int, y: Int, w: Int, h: Int, rotate: dir) -> UITextView {
        let inf = UITextView()
        inf.frame = CGRect(x: self.getX(x: x), y: self.getY(y: y), width: w, height: h)
        inf.text = txt
        inf.font = UIFont(name: inf.font!.fontName, size: 20)
        inf.isEditable = false
//        inf.backgroundColor = UIColor.red
        
        // scale to fit
//        inf.translatesAutoresizingMaskIntoConstraints = true
//        inf.sizeToFit()
        
        self.view.addSubview(inf)
        
        switch (rotate) {
        case .down:
            break
        case .left:
            inf.transform = CGAffineTransform(rotationAngle: CGFloat.pi / 2)
        case .up:
            inf.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
        case .right:
            inf.transform = CGAffineTransform(rotationAngle: -CGFloat.pi / 2)
        }
        
        return inf
    }

}
