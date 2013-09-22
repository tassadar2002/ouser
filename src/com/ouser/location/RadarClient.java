package com.ouser.location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.ouser.module.Location;

public class RadarClient {

	private static final byte CMD_OK = 0;
	//private static final byte CMD_NOOP = 0x01; // 心跳指令
	private static final byte CMD_DISCONNECTED = 0x02;// 断开连接指令
	private static final byte CMD_ANNOUNCE_SELF = 0x03; // 声明自身指令
	private static final byte CMD_ANNOUNCE_DEST = 0x04; // 声明目标指令
	private static final byte CMD_REPORT = 0x05;// 上报位置
	private static final byte CMD_REQUEST = 0x06;// 请求位置
	private static final byte CMD_ANSWER = 0x07;// 请求位置应答

	//private static final byte CMD_ERR = (byte) 0x80;// 未知错误
	private static final byte CMD_ERR_LOST = (byte) 0x81;// 用户断线

	private Socket client;
	private InputStream is;
	private OutputStream os;
	private String email;
	private String dest;

	public RadarClient(String email, String dest, String ip, int port) {
		try {
			this.email = email;
			this.dest = dest;
			client = new Socket(ip, port);
			is = client.getInputStream();
			os = client.getOutputStream();
		} catch (Exception e) {
			is = null;
			os = null;
		}
	}

	private byte[] revertBytes(byte[] b) {
		byte ret[] = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			ret[ret.length - i - 1] = b[i];
		}
		return ret;
	}

	private byte[] int2byte(int res) {
		byte[] targets = new byte[4];
		targets[3] = (byte) (res & 0xff);
		targets[2] = (byte) ((res >> 8) & 0xff);
		targets[1] = (byte) ((res >> 16) & 0xff);
		targets[0] = (byte) (res >>> 24);
		return targets;
	}

	private int byte2int(byte[] b) {
		try {
			int r = 0;
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream();
			pis.connect(pos);
			DataInputStream dis = new DataInputStream(pis);
			DataOutputStream dos = new DataOutputStream(pos);
			dos.write(b, 0, b.length);
			r = dis.readInt();
			dis.close();
			dos.close();
			return r;
		} catch (Exception e) {
			return -1;
		}
	}

	public boolean annouce() {
		if (is == null || os == null)
			return false;
		try {
			byte[] cmd = new byte[Math.max(email.length(), dest.length()) + 2];
			cmd[0] = RadarClient.CMD_ANNOUNCE_SELF;
			cmd[1] = (byte) email.length();
			byte[] b = email.getBytes("US-ASCII");
			for (int i = 0; i < b.length; i++) {
				cmd[i + 2] = b[i];
			}
			os.write(cmd);
			os.flush();

			Arrays.fill(cmd, (byte) 0x0);
			int len = is.read(cmd);
			if (len <= 0) {
				is.close();
				os.close();
				client.close();
				return false;
			}
			if (cmd[0] != RadarClient.CMD_OK) {
				is.close();
				os.close();
				client.close();
				return false;
			}

			Arrays.fill(cmd, (byte) 0x0);
			cmd[0] = RadarClient.CMD_ANNOUNCE_DEST;
			cmd[1] = (byte) dest.length();
			b = dest.getBytes("US-ASCII");
			for (int i = 0; i < b.length; i++) {
				cmd[i + 2] = b[i];
			}
			os.write(cmd);
			os.flush();

			Arrays.fill(cmd, (byte) 0x0);
			len = is.read(cmd);
			if (len <= 0) {
				is.close();
				os.close();
				client.close();
				return false;
			}
			if (cmd[0] != RadarClient.CMD_OK) {
				is.close();
				os.close();
				client.close();
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				is.close();
				os.close();
				client.close();
			} catch (Exception ee) {
			}
			return false;
		}
	}

	public boolean reportLocation(float lng, float lat) {
		try {
			byte[] cmd = new byte[9];
			cmd[0] = RadarClient.CMD_REPORT;
			byte[] tmp = int2byte(Float.floatToIntBits(lng));
			for (int i = 0; i < 4; i++) {
				cmd[i + 1] = tmp[i];
			}
			tmp = int2byte(Float.floatToIntBits(lat));
			for (int i = 0; i < 4; i++) {
				cmd[i + 5] = tmp[i];
			}
			os.write(cmd);
			os.flush();

			Arrays.fill(cmd, (byte) 0x0);
			int len = is.read(cmd);
			if (len <= 0) {
				is.close();
				os.close();
				client.close();
				return false;
			}
			if (cmd[0] != RadarClient.CMD_OK) {
				is.close();
				os.close();
				client.close();
				return false;
			}
			return true;
		} catch (Exception e) {
			try {
				is.close();
				os.close();
				client.close();
			} catch (Exception ee) {
			}
			return false;
		}
	}

	public Location requestLocation() {
		try {
			os.write(RadarClient.CMD_REQUEST);
			os.flush();

			byte[] cmd = new byte[9];
			is.read(cmd);
			if (cmd[0] == RadarClient.CMD_ERR_LOST) {
				return null;
			}
			if (cmd[0] != RadarClient.CMD_ANSWER)
				return null;

			byte[] lng = new byte[4];
			for (int i = 0; i < lng.length; i++)
				lng[i] = cmd[i + 1];
			lng = revertBytes(lng);
			byte[] lat = new byte[4];
			for (int i = 0; i < lat.length; i++)
				lat[i] = cmd[i + 5];
			lat = revertBytes(lat);

			return new Location(Float.intBitsToFloat(byte2int(lat)),
					Float.intBitsToFloat(byte2int(lng)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void disconnect() {
		try {
			os.write(RadarClient.CMD_DISCONNECTED);
			os.flush();
			is.close();
			os.close();
			client.close();
		} catch (Exception e) {

		}
	}
}
