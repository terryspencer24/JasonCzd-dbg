//
//  JoinGameTableViewCell.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/1/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class JoinGameTableViewCell: UITableViewCell {

    @IBOutlet weak var cellText: UILabel!
    
    @IBOutlet weak var gameInvites: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
