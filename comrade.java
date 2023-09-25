import java.applet.Applet;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

public class comrade extends Applet implements KeyListener, Runnable
{
	private int myX, myY;//���@���W
	private int enemX[], enemY[];//�G���W
	private int ammoX1[], ammoY1[], ammoX2[], ammoY2[];//���@�e���W
	private int enamX[], enamY[];//�G�e���W
	private int enamXmov[], enamYmov[];//�G�e�ړ���
	private int bosX, bosY;//�{�X���W
	private int bosexYsm=0, bosexYend=0;//�{�X�����p���W
	private int enmovX[], enmovY[];//�G�ړ���
	private int enexX[], enexY[], enexYsm[], enexYend[];//�G�������W
	private int meexX, meexY, meexYend, meexYsm;//���@�������W
	private int hitenem=0, hitme=0, hitboss=0, nextboss=50, bosscnt=0, hitleft=100;//�q�b�g�J�E���g��
	private int enemovtype[], enemtype, bosstype;//�G�^�C�v����p
	private int bosdir=0;//�{�X����
	private int score=0, subscore=100, bosscore=2000, hitrate=0, ammofired=0;//�X�R�A�p
	private int mapX=0, mapY=0, mvmp=0, wtmpmv=0;//�}�b�v�p
	private int enemCount;
		
	private boolean startCond=false, selectCond=false, stopCond=false, overCond=false;//�Q�[���R���f�B�V�����p
	private boolean ammoCond[], enemCond[], enexCond[], ensmCond[], enamCond[];//�e�E�����p�R���f�B�V�����p
	private boolean myexCond=false, mysmCond=false;//���@�����R���f�B�V�����p
	private boolean bossCond=false, bossexCond=false, bosssmCond=false;//�{�X�R���f�B�V�����p
	
	private boolean MOVUP = false;
	private boolean MOVDOWN = false;
	private boolean MOVRIGHT = false;
	private boolean MOVLEFT = false;
	
	Thread thComrade = null;
	Graphics offGraphics;
	Image offImage, me, boss, myexp, mysmoke;
	Image enem[], exp[], smoke[], map[][];
	Image su27, mig29, f15, f16, euf, f18, f117, b2, b52, sea, snd, stp, wd;
	Random rnd;
	
	public void init()
	{
		//���@�֌W�ϐ�������
		myX = 50;
		myY = 110;
		ammoX1 = new int[5];
		ammoY1 = new int[5];
		ammoX2 = new int[5];
		ammoY2 = new int[5];
		ammoCond = new boolean[5];
		
		//�G�֌W�ϐ�������
		enem = new Image[3];
		enemCond = new boolean[3];
		enemX = new int[3];
		enemY = new int[3];
		enmovX = new int[3];
		enmovY = new int[3];
		enemovtype = new int[3];
		enemCount=0;
		
		//�����֌W�ϐ�������
		exp = new Image[5];
		smoke = new Image[5];
		ensmCond = new boolean[5];
		enexCond = new boolean[5];
		enexX = new int[5];
		enexY = new int[5];
		enexYsm = new int[5];
		enexYend = new int[5];
		enamX = new int[6];
		enamY = new int[6];
		enamXmov = new int[6];
		enamYmov = new int[6];
		enamCond = new boolean[6];
		
		//�G�����摜�I�u�W�F�N�g��荞��
		for(int i=0;i<5;i++)
		{
			exp[i]=getImage(getDocumentBase(), "explosion.gif");
			smoke[i]=getImage(getDocumentBase(), "kemuri.gif");
		}
		
		//�摜�ǂݍ���
		myexp = getImage(getDocumentBase(), "explosion.gif");
		mysmoke = getImage(getDocumentBase(), "kemuri.gif");
		su27 = getImage(getDocumentBase(), "su27.gif");
		mig29 = getImage(getDocumentBase(), "mig29.gif");
		f15 = getImage(getDocumentBase(), "f15.gif");
		f16 = getImage(getDocumentBase(), "f16.gif");
		euf = getImage(getDocumentBase(), "euf.gif");
		f18 = getImage(getDocumentBase(), "f18.gif");
		f117 = getImage(getDocumentBase(), "f117.gif");
		b2 = getImage(getDocumentBase(), "b2.gif");
		b52 = getImage(getDocumentBase(), "b52.gif");
		sea = getImage(getDocumentBase(), "sea.gif");
		snd = getImage(getDocumentBase(), "snd.gif");
		stp = getImage(getDocumentBase(), "stp.gif");
		wd = getImage(getDocumentBase(), "wd.gif");
		
		rnd = new Random();//�����I�u�W�F�N�g
		
		addKeyListener(this);//�L�[���X�i�[�o�^
		
		thComrade = new Thread(this);//�X���b�h�I�u�W�F�N�g
		thComrade.start();//�X���b�h�X�^�[�g
		
		offImage = createImage(120, 130);
		offGraphics = offImage.getGraphics();
		
		//�}�b�v�p
		map = new Image[MapData.getMapLength()][24];
		mvmp = MapData.getMapLength()-26;
		
		for(int i=0;i<MapData.getMapLength();i++)//�}�b�v�������[���ۑ�
		{
			for(int ii=0;ii<24;ii++)
			{
				if((MapData.MapDataReturn(i,ii))==0)
				{
					map[i][ii]=sea;
				}
				else if((MapData.MapDataReturn(i,ii))==1)
				{
					map[i][ii]=snd;
				}
				else if((MapData.MapDataReturn(i,ii))==2)
				{
					map[i][ii]=stp;
				}
				else if((MapData.MapDataReturn(i,ii))==3)
				{
					map[i][ii]=wd;
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e)//�����L�[�C�x���g
	{
		if(!myexCond&&!selectCond&&!overCond) //��s�@�R���g���[��
		{
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD6)||(e.getKeyCode()==KeyEvent.VK_RIGHT))
			{
				MOVRIGHT=true;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD8)||(e.getKeyCode()==KeyEvent.VK_UP))
			{
				MOVUP=true;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD4)||(e.getKeyCode()==KeyEvent.VK_LEFT))
			{
				MOVLEFT=true;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD2)||(e.getKeyCode()==KeyEvent.VK_DOWN))
			{
				MOVDOWN=true;
			}
			
			if(e.getKeyCode()==KeyEvent.VK_SPACE)
			{
				fireammo();//�e���˃��[�`���Ăяo��
			}
		}
		
		if(!selectCond&&!startCond&&!overCond) //1�L�[�����A�Z���N�g��ʂ�
		{
			if(e.getKeyCode()==KeyEvent.VK_S)
			{
					selectCond=true;
					repaint();
			}
		}
		
		if(selectCond&&!startCond&&!overCond) //��s�@�I��
		{
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD1)||(e.getKeyCode()==KeyEvent.VK_1))
			{
				selectCond=false;
				me = mig29;
				startCond=true;
				repaint();
			}
		}
		
		if(stopCond&&startCond&&!overCond) //�Q�[���ĊJ
		{
			if(e.getKeyCode()==KeyEvent.VK_S)
			{
				stopCond=false;
				repaint();
			}
		}
		
		if(!selectCond&&!startCond&&overCond)//���g���C�E������
		{
			if(e.getKeyCode()==KeyEvent.VK_S)
			{
				overCond=false;
				selectCond=true;
				bossCond=false;
				for(int i=0;i<3;i++)
				{
					enemCond[i]=false;
				}
				for(int i=0;i<6;i++)
				{
					enamCond[i]=false;
				}
				mvmp = MapData.getMapLength()-24;
				mapX=0;
				mapY=0;
				mvmp=0;
				wtmpmv=0;
				ammofired=0;
				hitrate=0;
				hitenem=0;
				hitme=0;
				hitboss=0;
				nextboss=50;
				bosscnt=0;
				hitleft=100;
				score=0;
				subscore=100;
				bosscore=2000;
				repaint();
			}
		}
		
		if(selectCond&&!startCond&&!overCond) //2�L�[�����A��s�@�I��
		{
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD2)||(e.getKeyCode()==KeyEvent.VK_2))
			{
				selectCond=false;
				me = su27;
				startCond=true;
				repaint();
			}
		}
		
		if(startCond&&!selectCond&&!overCond) //�Q�[���X�g�b�v
		{
			if(e.getKeyCode()==KeyEvent.VK_P)
			{
				stopCond=true;
				repaint();
			}
		}
	}
	
	public void keyReleased(KeyEvent e)//�����L�[�C�x���g
	{
		if(!myexCond&&!selectCond&&!overCond) //��s�@�R���g���[��
		{
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD6)||(e.getKeyCode()==KeyEvent.VK_RIGHT))
			{
				MOVRIGHT=false;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD8)||(e.getKeyCode()==KeyEvent.VK_UP))
			{
				MOVUP=false;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD4)||(e.getKeyCode()==KeyEvent.VK_LEFT))
			{
				MOVLEFT=false;
			}
			
			if((e.getKeyCode()==KeyEvent.VK_NUMPAD2)||(e.getKeyCode()==KeyEvent.VK_DOWN))
			{
				MOVDOWN=false;
			}
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void getkeycond()//���@�ړ���ԃ`�F�b�N
	{
		if(MOVUP)
		{
			movme(-90);
		}
		
		if(MOVDOWN)
		{
			movme(90);
		}
		
		if(MOVRIGHT)
		{
			movme(0);
		}
		if(MOVLEFT)
		{
			movme(-180);
		}
		
		if(MOVUP&&MOVRIGHT)
		{
			movme(-45);
		}
		
		if(MOVUP&&MOVLEFT)
		{
			movme(-135);
		}
		
		if(MOVDOWN&&MOVRIGHT)
		{
			movme(45);
		}
		
		if(MOVDOWN&&MOVLEFT)
		{
			movme(135);
		}
		
		if(myY>110)
		{
			myY=110;
		}
			
		if(myX>100)
		{
			myX=100;
		}
			
		if(myY<0)
		{
			myY=0;
		}
			
		if(myX<0)
		{
			myX=0;
		}
	}
	
	public void movme(int degr)//���@�ړ�
	{
		myX+= (int)(Math.cos(Math.toRadians(degr)))*2;
		myY+= (int)(Math.sin(Math.toRadians(degr)))*2;
	}
	
	public void paint(Graphics g) //��{�`�ʃ��[�`��
	{
		if(selectCond&&!startCond&&!overCond) //���@�Z���N�g��ʕ`��
		{
			offGraphics.setColor(Color.black);
			offGraphics.fillRect(0,0,120,130);
			offGraphics.setColor(Color.green);
			offGraphics.drawString("SELECT AIRCRAFT",0,30);
			offGraphics.drawString("1:MIG29 FULCRUM",0,60);
			offGraphics.drawString("2:SU27 FLANKER",0,80);
			g.drawImage(offImage,0,0,this);
		}
			
		if(overCond&&!startCond&&!selectCond)//�Q�[���I�[�o�[��ʕ`��
		{
			offGraphics.setColor(Color.black);
			offGraphics.fillRect(0,0,120,130);
			offGraphics.setColor(Color.green);
			offGraphics.drawString("GAME OVER",25,20);
			offGraphics.drawString("SCORE:"+(""+score),25,40);
			offGraphics.drawString("FIRE:"+(""+ammofired),25,50);
			offGraphics.drawString("HIT:"+(""+hitenem),25,60);
			offGraphics.drawString("RATE:"+(""+hitrate)+"%",25,70);
			offGraphics.drawString("S:RETRY",25,100);
			g.drawImage(offImage,0,0,this);
		}
			
		if(startCond&&!selectCond&&!overCond) //�Q�[����ʕ`��
		{
			offGraphics.setColor(Color.black);
			offGraphics.fillRect(0,0,120,130);
			offGraphics.setColor(Color.green);
			
			for(int i=0;i<26;i++)//�}�b�v�`��
			{
				for(int ii=0;ii<24;ii++)
				{
					offGraphics.drawImage(map[mvmp+i][ii],mapX,mapY,this);
						
					mapX+=5;
						
					if(mapX==120)
					{
						mapX=0;
					}
				}
				mapY+=5;
				if(mapY==130)
				{
					mapY=0;
				}
			}
			
			//�_���[�W�C���f�B�P�[�^�`��
			if(!myexCond)
			{
				offGraphics.setColor(Color.black);
				offGraphics.drawRect(10,125,100,3);
				offGraphics.setColor(Color.red);
				offGraphics.fillRect(10,125,hitleft,3);
			}
			
			for(int i=0;i<3;i++)//�G�`��
			{
				if(enemCond[i])
				{
					offGraphics.drawImage(enem[i], enemX[i], enemY[i],this);
				}
			}
			
			for(int i=0;i<3;i++)//�G�����`��
			{
				if(enexCond[i])
				{
					if(ensmCond[i])
					{
						offGraphics.drawImage(smoke[i], enexX[i], enexY[i],this);
					}
					else
					{
						offGraphics.drawImage(exp[i], enexX[i], enexY[i],this);
					}
				}
			}
			
			if(!myexCond) //���@�`��
			{
				offGraphics.drawImage(me, myX, myY,this);
			}
				
			if(myexCond)//���@�����`��
			{
				if(mysmCond)
				{
					offGraphics.drawImage(mysmoke, meexX, meexY,this);
				}
				else
				{
					offGraphics.drawImage(myexp, meexX, meexY,this);
				}
			}
			
			if(bossCond)//�{�X�`��
			{
				offGraphics.drawImage(boss, bosX, bosY,this);
			}
			
			if(bossexCond)//�{�X�����`��
			{
				if(bosssmCond)
				{
					offGraphics.drawImage(smoke[4], enexX[4], enexY[4],this);
					offGraphics.drawImage(smoke[4], (enexX[4]+20), enexY[4],this);
					offGraphics.drawImage(smoke[4], enexX[4], (enexY[4]+20),this);
					offGraphics.drawImage(smoke[4], (enexX[4]+20), (enexY[4]+20),this);
				}
				else
				{
					offGraphics.drawImage(exp[4], enexX[4], enexY[4],this);
					offGraphics.drawImage(exp[4], (enexX[4]+20), enexY[4],this);
					offGraphics.drawImage(exp[4], enexX[4], (enexY[4]+20),this);
					offGraphics.drawImage(exp[4], (enexX[4]+20), (enexY[4]+20),this);
				}
			}
			
			for(int i=0; i<5; i++)//���@�e�`��
			{
				if(ammoCond[i])
				{
					offGraphics.setColor(Color.gray);
					offGraphics.fillRect(ammoX1[i],ammoY1[i],2,10);
					offGraphics.fillRect(ammoX2[i],ammoY2[i],2,10);
					offGraphics.setColor(Color.yellow);
					offGraphics.fillRect(ammoX1[i],(ammoY1[i]+6),2,4);
					offGraphics.fillRect(ammoX2[i],(ammoY2[i]+6),2,4);
					offGraphics.setColor(Color.red);
					offGraphics.fillRect(ammoX1[i],(ammoY1[i]+8),2,2);
					offGraphics.fillRect(ammoX2[i],(ammoY2[i]+8),2,2);
				}
			}
			
			for(int i=0; i<6; i++)//�G�e�`��
			{
				if(enamCond[i])
				{
					offGraphics.setColor(Color.pink);
					offGraphics.fillRect(enamX[i],enamY[i],2,5);
					offGraphics.setColor(Color.red);
					offGraphics.fillRect(enamX[i],enamY[i],2,3);
				}
			}
				
			offGraphics.setColor(Color.yellow);
			if(!stopCond) //�����Q�[�����f�łȂ���Γ��_�\��
			{
				offGraphics.drawString("SCORE:"+(""+score),0,10);
			}
			else //���������Ȃ��ԕ\��
			{
				offGraphics.drawString("GAME PAUSE",5,10);
			}
				
			g.drawImage(offImage,0,0,this);
		}
		
		if(!selectCond&&!startCond&&!overCond) //�^�C�g����ʕ`��
		{
			offGraphics.setColor(Color.black);
			offGraphics.fillRect(0,0,120,130);
			offGraphics.setColor(Color.green);
			offGraphics.drawString("COMRADE",30,30);
			offGraphics.drawString("START S KEY ",25,60);
			offGraphics.drawString("(c)2003",35,100);
			offGraphics.drawString("Y&Y FACTORY",25,120);
			g.drawImage(offImage,0,0,this);
		}
	}
		
	public void update(Graphics g)
	{
		paint(g);
	}
		
	public void mainloop() //���C�����[�v
	{
		getkeycond();
		makeenemy();//�G���Y���[�`��
		movenem();//�G�ړ����[�`��
		enamfire();//�G�e���˃��[�`��
		updatemyammo();//�e�ړ��E��ԃ`�F�b�N���[�`��
		chkhitenem();//�G�q�b�g�`�F�b�N���[�`��
		makeboss();//�{�X�o�����[�`��
		chkenexcond();//�G������ԃ`�F�b�N���[�`��
		updatebosinfo();//�{�X�ړ����[�`��
		bosamfire();////�{�X�e���˃��[�`��
		ammov();//�G�e�ړ����[�`��
		chkenamhitme();//�G�e�q�b�g���胋�[�`��
		chkhitboss();//�{�X�q�b�g�`�F�b�N���[�`��
		updatebosexinfo();//�{�X������ԃ`�F�b�N���[�`��
		chkoverlapenem();//���@�E�G�d�Ȃ�`�F�b�N���[�`��
		chkoverlapbos();//���@�E�{�X�d�Ȃ�`�F�b�N���[�`��
		chkmydamage();//���@�_���[�W�󋵃`�F�b�N���[�`��
		chkmyexp();//���@�����󋵃`�F�b�N���[�`��
		chkmapstatus();//�}�b�v�X�e�[�^�X�`�F�b�N���[�`��
		
		repaint(); //�^�C���A�E�g��ĕ`��
	}
	
	public void chkmapstatus()//�}�b�v�X�e�[�^�X�`�F�b�N���[�`��
	{
		if(startCond&&!myexCond)
		{
			wtmpmv++;
			if(wtmpmv>10)
			{
				mvmp--;
				wtmpmv=0;
			}
			if(mvmp==0)
			{
				mvmp=MapData.getMapLength()-26;
			}
		}
	}
	
	public void fireammo()//�e���˃��[�`��
	{
		for(int i=0; i<5; i++)
		{
			if(!ammoCond[i])
			{
				ammofired++;
				ammoCond[i]=true;
				ammoX1[i]=myX+2;
				ammoX2[i]=myX+16;
				ammoY1[i]=myY;
				ammoY2[i]=myY;
				break;
			}
		}
	}
	
	public void updatemyammo()//�e�ړ��E��ԃ`�F�b�N���[�`��
	{
		for(int i=0; i<5; i++)
		{
			if(ammoCond[i])
			{
				ammoY1[i]-=5;
				ammoY2[i]-=5;
				if(ammoY1[i]<0||ammoY2[i]<0)
				{
					ammoX1[i]=0;
					ammoX2[i]=0;
					ammoY1[i]=0;
					ammoY2[i]=0;
					ammoCond[i]=false;
				}
			}
		}
	}
	
	public void makeenemy()//�G���Y���[�`��
	{
		int destX=0;
		
		enemCount++;
		
		if((!enemCond[0])&&(!enemCond[1])&&(!enemCond[2])&&(enemCount>15))
		{
			enemCount=0;
			for(int i=0;i<3;i++)
			{
				if(!enemCond[i])
				{
					enemCond[i]=true;
					
					enemtype = Math.abs(rnd.nextInt()) % 5;//�G�^�C�v���藐������
					
					if(enemtype==0)
					{
						enem[i]=f15;
					}
					else if(enemtype==1)
					{
						enem[i]=f16;
					}
					else if(enemtype==2)
					{
						enem[i]=euf;
					}
					else if(enemtype==3)
					{
						enem[i]=f18;
					}
					else if(enemtype==4)
					{
						enem[i]=f117;
					}
					
					enemovtype[i] = Math.abs(rnd.nextInt()) % 2;//�G�������藐������
					
					//�G�o���ʒu����
					if((enemovtype[i]==0)&&((enemtype==0)||(enemtype==1)||(enemtype==2)))
					{
						enemY[i]=0;
						do
						{
							enemX[i] = Math.abs(rnd.nextInt()) % 120;
						}
						while(enemX[i]>100);
						
						enmovX[i]=0;
						enmovY[i]=2;
					}
					else if((enemovtype[i]==1)&&((enemtype==0)||(enemtype==1)||(enemtype==2)))
					{
						enemY[i]=0;
						do
						{
							enemX[i] = Math.abs(rnd.nextInt()) % 120;
						}
						while(enemX[i]>100);
						
						enmovX[i]=(int)((myX-enemX[i])/40);
						enmovY[i]=2;
					}
					else
					{
						if(enemovtype[i]==1)
						{
							enemX[i] = 120;
							
							do
							{
								enemY[i] = Math.abs(rnd.nextInt()) % 130;
							}
							while((enemY[i]>80)||(enemY[i]<50));
							
							enmovX[i]=(int)(((((Math.abs(rnd.nextInt()) % 130)/2)-enemX[i])/40));
							enmovY[i]=(int)((0-enemY[i])/40);
						}
						else 
						{
							enemX[i] = 0;
							
							do
							{
								enemY[i] = Math.abs(rnd.nextInt()) % 130;
							}
							while((enemY[i]>80)||(enemY[i]<50));
							
							do
							{
								destX=Math.abs(rnd.nextInt()) % 120;
							}
							while(destX<75);
							
							enmovX[i]=(int)((destX-enemX[i])/40);
							enmovY[i]=(int)((0-enemY[i])/40);
						}
					}
				}
			}
		}
		else if(enemCount>15)
		{
			enemCount=0;
		}
	}
	
	public void movenem()//�G�ړ����[�`��
	{
		for(int i=0;i<3;i++)
		{
			if(enemCond[i])
			{
				enemX[i]+=enmovX[i];
				enemY[i]+=enmovY[i];
					
				if((enemY[i]>115)||(enemX[i]>100))
				{
					enemCond[i]=false;
				}
				else if(((enem[i]==getImage(getDocumentBase(), "f18.gif"))||(enem[i]==getImage(getDocumentBase(), "f117.gif")))&&enemY[i]<0)
				{
					enemCond[i]=false;
				}
			}
		}
	}
	
	public void chkhitenem()//�G�q�b�g�`�F�b�N���[�`��
	{
		for(int i=0;i<5;i++)
		{
			if(ammoCond[i])
			{
				for(int ii=0;ii<3;ii++)
				{
					if(enemCond[ii]&&!enexCond[ii])
					{
						if((ammoX1[i]>enemX[ii])&&((ammoX1[i]+2)<(enemX[ii]+20))&&(ammoY1[i]>enemY[ii])&&(ammoY1[i]<(enemY[ii]+25)))
						{//���e���q�b�g�̏ꍇ
							getenemexini(i,ii);
						}
						else if((ammoX1[i]>enemX[ii])&&((ammoX1[i]+2)<(enemX[ii]+20))&&((ammoY1[i]+10)>enemY[ii])&&(ammoY1[i]<enemY[ii]))
						{//���e��q�b�g�̏ꍇ
							getenemexini(i,ii);
						}
						else if((ammoX2[i]>enemX[ii])&&((ammoX2[i]+2)<(enemX[ii]+20))&&(ammoY2[i]>enemY[ii])&&(ammoY2[i]<(enemY[ii]+25)))
						{//�E�e���q�b�g�̏ꍇ
							getenemexini(i,ii);
						}
						else if((ammoX2[i]>enemX[ii])&&((ammoX2[i]+2)<(enemX[ii]+20))&&((ammoY2[i]+10)>enemY[ii])&&(ammoY2[i]<enemY[ii]))
						{//�E�e��q�b�g�̏ꍇ
							getenemexini(i,ii);
						}
					}
				}
			}
		}
	}
	
	public void getenemexini(int am, int en)//�G�����p�ϐ��i�[���[�`��
	{
		if(!bossCond)
		{
			bosscnt++;
		}
		
		hitenem++;
		score+=subscore;
		enexCond[en]=true;
		ammoCond[am]=false;
		enexX[en]=enemX[en];
		enexY[en]=enemY[en];
		enexYsm[en]=enemY[en]+3;
		enexYend[en]=enemY[en]+6;
		enemCond[en]=false;
	}
	
	public void chkenexcond()//�G������ԃ`�F�b�N���[�`��
	{
		for(int i=0;i<3;i++)
		{
			if(enexCond[i])
			{
				enexY[i]++;
				if(enexY[i]>enexYsm[i])
				{
					ensmCond[i]=true;
					if(enexY[i]>enexYend[i])
					{
						ensmCond[i]=false;
						enexCond[i]=false;
					}
				}
			}
		}
	}
	
	public void makeboss()//�{�X�o�����[�`��
	{
		if((bosscnt>nextboss)&&!bossCond)
		{
			bosstype = Math.abs(rnd.nextInt()) % 2;
			
			if(bosstype==0)
			{
				bosX=40;
				bosY=-35;
				boss = b52;
			}
			else if(bosstype==1)
			{
				bosY=-20;
				bosX=47;
				boss = b2;
			}
				
			bossCond=true;
			nextboss+=50;
		}
	}
	
	public void updatebosinfo()//�{�X�ړ����[�`��
	{
		if(bossCond)
		{
			if(bosY<20)
			{
				bosY++;
			}
			else 
			{
				if(bosdir==0)
				{
					bosX++;
					if(bosX>70)
					{
						bosdir=1;
					}
				}
				else
				{
					bosX--;
					if(bosX<30)
					{
						bosdir=0;
					}
				}
			}
		}
	}
	
	public void chkhitboss()//�{�X�q�b�g�`�F�b�N���[�`��
	{
		if(bossCond)
		{
			for(int i=0;i<5;i++)
			{
				if(ammoCond[i])
				{
					if(bosstype==0)
					{
						if((ammoX1[i]>bosX)&&((ammoX1[i]+2)<(bosX+40))&&(ammoY1[i]>bosY)&&(ammoY1[i]<(bosY+35)))
						{//�E�e�p
							ammoCond[i]=false;
							hitboss++;
							hitenem++;
						}
						else if((ammoX2[i]>bosX)&&((ammoX2[i]+2)<(bosX+40))&&(ammoY2[i]>bosY)&&(ammoY2[i]<(bosY+35)))
						{//���e�p
							ammoCond[i]=false;
							hitboss++;
							hitenem++;
						}
					}
					else
					{
						if((ammoX1[i]>bosX)&&((ammoX1[i]+2)<(bosX+35))&&(ammoY1[i]>bosY)&&(ammoY1[i]<(bosY+20)))
						{//�E�e�p
							ammoCond[i]=false;
							hitboss++;
							hitenem++;
						}
						else if((ammoX2[i]>bosX)&&((ammoX2[i]+2)<(bosX+35))&&(ammoY2[i]>bosY)&&(ammoY2[i]<(bosY+20)))
						{//���e�p
							ammoCond[i]=false;
							hitboss++;
							hitenem++;
						}
					}
				}
			}
		}
	}
	
	public void updatebosexinfo()//�{�X������ԃ`�F�b�N���[�`��
	{
		if(bossCond)
		{
			if(hitboss>50)
			{
				for(int i=3;i<6;i++)
				{
					enamCond[i]=false;
				}
				
				hitboss=0;
				score+=bosscore;
				bosscore+=1000;
				enexX[4]=bosX;
				enexY[4]=bosY;
				bosexYsm=bosY+10;
				bosexYend=bosY+20;
				bossCond=false;
				bossexCond=true;
			}
		}
		else if(bossexCond)
		{
			enexY[4]++;
			if(enexY[4]>bosexYsm)
			{
				bosssmCond=true;
				if(enexY[4]>bosexYend)
				{
					bosssmCond=false;
					bossexCond=false;
				}
			}
		}
	}
	
	public void chkenamhitme()//�G�e�q�b�g���胋�[�`��
	{
		for(int i=0;i<6;i++)
		{
			if(enamCond[i])
			{
				if((enamX[i]>myX)&&((enamX[i]+2)<(myX+19))&&((enamY[i]+5)>(myY+5))&&((enamY[i]+5)<(myY+19)))
				{
					hitme++;
					hitleft-=2;
					if(hitleft==0)
					{
						hitleft=100;
					}
					enamCond[i]=false;
				}
			}
		}
	}
	
	public void chkoverlapenem()//���@�E�G�d�Ȃ�`�F�b�N���[�`��
	{
		for(int i=0;i<3;i++)
		{
			if(enemCond[i]&&!myexCond)
			{
				if((myX>enemX[i])&&(myX<(enemX[i]+20))&&((myY+5)>enemY[i])&&(myY<(enemY[i]+25)))
				{//�E��q�b�g
					getmyexini(i);
				}
				else if((enemX[i]>myX)&&(enemX[i]<(myX+19))&&((myY+5)>enemY[i])&&(myY<(enemY[i]+25)))
				{//����q�b�g
					getmyexini(i);
				}
				else if((myX>enemX[i])&&(myX<(enemX[i]+20))&&((myY+19)>(enemY[i]+5))&&(myY<enemY[i]))
				{//�E���q�b�g
					getmyexini(i);
				}
				else if((enemX[i]>myX)&&(enemX[i]<(myX+19))&&((myY+19)>(enemY[i]+5))&&(myY<enemY[i]))
				{//�����q�b�g
					getmyexini(i);
				}
			}
		}
	}
	
	public void getmyexini(int mx)//���@�_���[�W���[�`��
	{
		hitme++;
		hitleft-=2;
		if(hitleft==0)
		{
			hitleft=100;
		}
		enemCond[mx]=false;
		enamCond[mx]=false;
	}
	
	public void chkoverlapbos()//���@�E�{�X�d�Ȃ�`�F�b�N���[�`��
	{
		if(bossCond&&!myexCond)
		{
			if(bosstype==0)
			{
				if((myX>bosX)&&(myX<(bosX+40))&&(myY>bosY)&&(myY<(bosY+35)))
				{//�E��q�b�g
					getmyexinibs();
				}
				else if((bosX>myX)&&(bosX<(myX+19))&&(myY>bosY)&&(myY<(bosY+35)))
				{//����q�b�g
					getmyexinibs();
				}
				else if((myX>bosX)&&(myX<(bosX+40))&&((myY+19)>bosY)&&(myY<bosY))
				{//�E���q�b�g
					getmyexinibs();
				}
				else if((bosX>myX)&&(bosX<(myX+19))&&((myY+19)>bosY)&&(myY<bosY))
				{//�����q�b�g
					getmyexinibs();
				}
			}
			else
			{
				if((myX>bosX)&&(myX<(bosX+35))&&(myY>bosY)&&(myY<(bosY+20)))
				{//�E��q�b�g
					getmyexinibs();
				}
				else if((bosX>myX)&&(bosX<(myX+20))&&(myY>bosY)&&(myY<(bosY+20)))
				{//����q�b�g
					getmyexinibs();
				}
				else if((myX>bosX)&&(myX<(bosX+35))&&((myY+20)>bosY)&&(myY<bosY))
				{//�E���q�b�g
					getmyexinibs();
				}
				else if((bosX>myX)&&(bosX<(myX+20))&&((myY+20)>bosY)&&(myY<bosY))
				{//�����q�b�g
					getmyexinibs();
				}
			}
		}
	}
	
	public void getmyexinibs()//���@�����p���������[�`���i�{�X�p)
	{
		hitme++;
		hitleft-=2;		
		if(hitleft==0)
		{
			hitleft=100;
		}
	}
	
	public void chkmyexp()//���@�����󋵃`�F�b�N���[�`��
	{
		if(myexCond)
		{
			meexY++;
			if(meexY>meexYsm)
			{
				mysmCond=true;
				if(meexY>meexYend)
				{
					myX = 50;
					myY = 110;
					hitleft=100;
					myexCond=false;
					mysmCond=false;
					startCond=false;
					overCond=true;//�Q�[���I��
					repaint();
				}
			}
		}
	}
	
	public void bosamfire()//�{�X�e���˃��[�`��
	{
		if(bossCond&&bosY>19)
		{
			for(int i=3;i<6;i++)
			{
				if(!enamCond[i])
				{
					if(bosstype==0)
					{
						enamX[i]=bosX+19;
						enamY[i]=bosY+35;
						enamXmov[i]=(int)((Math.abs(rnd.nextInt()) % 120)/40);
						enamYmov[i]=3;
						enamCond[i]=true;
						break;
					}
					else
					{
						enamX[i]=bosX+16;
						enamY[i]=bosY+20;
						enamXmov[i]=(int)((Math.abs(rnd.nextInt()) % 120)/40);
						enamYmov[i]=3;
						enamCond[i]=true;
						break;
					}
				}
			}
		}
	}
	
	public void enamfire()//�G�e���˃��[�`��
	{
		for(int i=0;i<3;i++)
		{
			if(!enamCond[i]&&enemCond[i])
			{
				if((!(enem[i].equals(f18)))||(!(enem[i].equals(f117))))
				{
					if((enemY[i]>10)&&(enemY[i]<20)&&(enemovtype[i]==0))
					{
						enamX[i]=enemX[i]+10;
						enamY[i]=enemY[i]+20;
						enamXmov[i]=(int)(((myX+10)-(enemX[i]+10))/35);
						enamYmov[i]=3;
						enamCond[i]=true;
					}
				}
			}
		}
	}
	
	public void ammov()//�G�e�ړ����[�`��
	{
		for(int i=0;i<6;i++)
		{
			if(enamCond[i])
			{
				enamX[i]+=enamXmov[i];
				enamY[i]+=enamYmov[i];
				if((enamX[i]>118)||(enamY[i]>125))
				{
					enamCond[i]=false;
				}
			}
		}
	}
	
	public void chkmydamage()//���@�_���[�W�󋵃`�F�b�N���[�`��
	{
		if(hitme>50)
		{
			hitme = 0;
			hitrate = (int)(hitenem*100/ammofired);
			meexX=myX;
			meexY=myY;
			meexYsm=myY+15;
			meexYend=myY+30;
			myexCond=true;
		}
	}
	
	public void run()
	{
		while(thComrade != null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch(Exception ex)
			{
			}
			
			if(startCond&&!stopCond)
			{
				mainloop();
			}
		}
	}
}