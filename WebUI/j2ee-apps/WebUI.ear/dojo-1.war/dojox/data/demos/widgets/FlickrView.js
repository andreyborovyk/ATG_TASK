if(!dojo._hasResource["dojox.data.demos.widgets.FlickrView"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.data.demos.widgets.FlickrView"] = true;
dojo.provide("dojox.data.demos.widgets.FlickrView");
dojo.require("dijit._Templated");
dojo.require("dijit._Widget");

dojo.declare("dojox.data.demos.widgets.FlickrView", [dijit._Widget, dijit._Templated], {
	//Simple demo widget for representing a view of a Flickr Item.

	templateString:"<table class=\"flickrView\">\r\n\t<tbody>\r\n\t\t<tr class=\"flickrTitle\">\r\n\t\t\t<td>\r\n\t\t\t\t<b>\r\n\t\t\t\t\tTitle:\r\n\t\t\t\t</b>\r\n\t\t\t</td>\r\n\t\t\t<td dojoAttachPoint=\"titleNode\">\r\n\t\t\t</td>\r\n\t\t</tr>\r\n\t\t<tr>\r\n\t\t\t<td>\r\n\t\t\t\t<b>\r\n\t\t\t\t\tAuthor:\r\n\t\t\t\t</b>\r\n\t\t\t</td>\r\n\t\t\t<td dojoAttachPoint=\"authorNode\">\r\n\t\t\t</td>\r\n\t\t</tr>\r\n\t\t<tr>\r\n\t\t\t<td colspan=\"2\">\r\n\t\t\t\t<b>\r\n\t\t\t\t\tImage:\r\n\t\t\t\t</b>\r\n\t\t\t</td>\r\n\t\t</tr>\r\n\t\t<tr>\r\n\t\t\t<td dojoAttachPoint=\"imageNode\" colspan=\"2\">\r\n\t\t\t</td>\r\n\t\t</tr>\r\n\t</tbody>\r\n</table>\r\n\r\n",

	//Attach points for reference.
	titleNode: null, 
	descriptionNode: null,
	imageNode: null,
	authorNode: null,

	title: "",
	author: "",
	imageUrl: "",
	iconUrl: "",

	postCreate: function(){
		this.titleNode.appendChild(document.createTextNode(this.title));
		this.authorNode.appendChild(document.createTextNode(this.author));
		var href = document.createElement("a");
		href.setAttribute("href", this.imageUrl);
		href.setAttribute("target", "_blank");
        var imageTag = document.createElement("img");
		imageTag.setAttribute("src", this.iconUrl);
		href.appendChild(imageTag);
		this.imageNode.appendChild(href);
	}
});

}
