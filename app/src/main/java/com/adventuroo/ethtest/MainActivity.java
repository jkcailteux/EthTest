package com.adventuroo.ethtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.NodeInfo;

public class MainActivity extends AppCompatActivity {

	private TextView textbox;
	private String starterText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textbox = (TextView) findViewById(R.id.textview);

		try {
			Node node = Geth.newNode(getFilesDir() + "/.ethereum", new NodeConfig());
			node.start();

			org.ethereum.geth.Context gethContext = new Context();

			NodeInfo info = node.getNodeInfo();
			starterText += "My name: " + info.getName() + "\n";
			starterText += "My address: " + info.getListenerAddress() + "\n";
			starterText += "My protocols: " + info.getProtocols() + "\n\n";

			EthereumClient ec = node.getEthereumClient();
			starterText += "Latest block: " + ec.getBlockByNumber(gethContext, -1).getNumber() + ", syncing...\n";

			NewHeadHandler handler = new NewHeadHandler() {
				@Override
				public void onError(String error) {
				}

				@Override
				public void onNewHead(final Header header) {
					MainActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							textbox.setText(starterText +  "#" + header.getNumber() + ": " + header.getHash().getHex().substring(0, 10) + "…\n");
						}
					});
				}
			};
			ec.subscribeNewHead(gethContext, handler, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		textbox.setText(starterText);

	}
}
