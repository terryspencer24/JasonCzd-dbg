//
//  CreateGameTableViewCell.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/30/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class CreateGameTableViewCell: UITableViewCell {
    
    @IBOutlet weak var inviteeName: UILabel!
    
    @IBOutlet weak var pencilImage: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
