package br.gov.incra.sicop;

import java.util.ArrayList;
import java.util.List;

import br.gov.incra.sicop.abstractactivity.IActivity;
import br.gov.incra.sicop.controller.GlobalController;
import br.gov.incra.sicop.list.ListViewImageAdapter;
import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProcessoActivity extends Activity implements IActivity {

    private ProgressDialog pd;
    
    private TextView tipo, nome, localizacao, numero, cadastro_pessoa, subnome,endereco,contato,gleba,municipio, classificacao;
    private TextView pecas,pecas_dados, pendencias, pendencias_dados, anexos, anexos_dados, movimentacoes, movimentacoes_dados, titulo, titulo_dados;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processo);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		init();

		SQLiteDatabase sql = ((GlobalController) getApplication()).getDatabase();
		if ( sql.isOpen() )
		{
			try
			{
				Cursor resultSet = sql.rawQuery("SELECT * FROM processo WHERE id = "+ ((GlobalController)getApplication()).getIdPesquisa() +"", null);
				resultSet.moveToFirst();
	
				tipo.setText( tipo.getText().toString()+" "+resultSet.getString(6) );
				numero.setText( numero.getText().toString()+" "+resultSet.getString(1) );
				if(resultSet.getString(6).equals("PROCESSO RURAL"))
				{
					nome.setText( "Requerente: "+resultSet.getString(3) );
					
					String cpf = resultSet.getString(2);
					String cpf_mask = "";
					for(int x=0;x<cpf.length();x++){
						if(x == 0 || x == 1  || x == 2 || x == cpf.length()-1 || x == cpf.length()-2)
							cpf_mask += "*";
						else
							cpf_mask += ""+cpf.charAt(x);
					}
					cadastro_pessoa.setText( "CPF: "+cpf_mask );
				}
				else if(resultSet.getString(6).equals("REGULARIZACAO URBANA"))
				{
					nome.setText( "Povoado: "+resultSet.getString(3) );
					String cnpj = resultSet.getString(2);
					String cnpj_mask = "";
					for(int x=0;x<cnpj.length();x++){
						if(x == 0 || x == 1 || x == cnpj.length()-2 || x == cnpj.length()-1)
							cnpj_mask += "*";
						else
							cnpj_mask += ""+cnpj.charAt(x);
					}
					cadastro_pessoa.setText( "CNPJ: "+cnpj_mask );
				}
				else
				{
					nome.setText( "Requerente: "+resultSet.getString(3) );
					String cpf = resultSet.getString(2);
					String cpf_mask = "";
					for(int x=0;x<cpf.length();x++){
						if(x == 0 || x == 1  || x == 2 || x == cpf.length()-2 || x == cpf.length()-1)
							cpf_mask += "*";
						else
							cpf_mask += ""+cpf.charAt(x);
					}
					cadastro_pessoa.setText( "CPF: "+cpf_mask );
				}
				localizacao.setText( localizacao.getText().toString()+" "+resultSet.getString(5) );
				
				gleba.setText( gleba.getText().toString()+" "+resultSet.getString(8) );
				endereco.setText( endereco.getText().toString()+" "+resultSet.getString(10) );
				contato.setText( contato.getText().toString()+" "+resultSet.getString(11) );
				municipio.setText( municipio.getText().toString()+" "+resultSet.getString(9) );
				if(resultSet.getString(6).equals("PROCESSO RURAL"))
					subnome.setText( "Cônjuge: "+resultSet.getString(4) );
				else if(resultSet.getString(6).equals("REGULARIZACAO URBANA"))
					subnome.setText( "Proj. Assentamento: "+resultSet.getString(4) );
				else
					subnome.setText( "Interessado: "+resultSet.getString(4) );
				
				
				if(resultSet.getString(7).equals("Pai."))
					classificacao.setVisibility(View.INVISIBLE);
				else
				{
					String[] principal = resultSet.getString(7).split("\\.");
					classificacao.setText( classificacao.getText().toString() + "\n\nPrincipal: ");
					for(int x=0;x<principal.length;x++)
					{
						switch (x) {
						case 1:
						{
							classificacao.setText( classificacao.getText().toString() + resultSet.getString(7).split("\\.")[1] );							
							break;
						}
						case 2:
						{
							classificacao.setText( classificacao.getText().toString() + " - " + resultSet.getString(7).split("\\.")[2] );							
							break;
						}
						default:
							break;
						}
					}
				}
				
				//DADOS AUXILIARES DO ANDAMENTO DO PROCESSO
				//PEÇAS
				if(resultSet.getString(15).equals(""))
					pecas_dados.setText("Nenhuma peça técnica.");
				else
				{
					String[] objpecas = resultSet.getString(15).split("FIMREG");
					for( int x = 0; x < objpecas.length;x++ )
					{
						String[] atribpecas = objpecas[x].split("\\|");						
						if(x>0)
							pecas_dados.setText( pecas_dados.getText().toString() + "\n---\n" );
						pecas_dados.setText( pecas_dados.getText().toString() + "Área: "+ atribpecas[0] +" ha\n" );
						pecas_dados.setText( pecas_dados.getText().toString() + "Município: "+ atribpecas[1] +"\n" );
						pecas_dados.setText( pecas_dados.getText().toString() + "Gleba: "+ atribpecas[2] +"\n" );
						pecas_dados.setText( pecas_dados.getText().toString() + "Contrato: "+ atribpecas[3] +"\n" );
						pecas_dados.setText( pecas_dados.getText().toString() + "Obs.: "+ atribpecas[4] );						
					}
				}
				//ANEXOS
				if(resultSet.getString(14).equals(""))
					anexos_dados.setText("Nenhum anexo.");
				else
				{
					String[] objanexos = resultSet.getString(14).split("FIMREG");
					for( int x = 0; x < objanexos.length;x++ )
					{
						String[] atribanexos = objanexos[x].split("\\|");						
						if(x>0)
							anexos_dados.setText( anexos_dados.getText().toString() + "\n---\n" );
						anexos_dados.setText( anexos_dados.getText().toString() + "Número: "+ atribanexos[0] +"\n" );
						anexos_dados.setText( anexos_dados.getText().toString() + "Requerente: "+ atribanexos[2] +"\n" );
						anexos_dados.setText( anexos_dados.getText().toString() + "Tipo: "+ atribanexos[1] +"\n" );
						anexos_dados.setText( anexos_dados.getText().toString() + "Usuário: "+ atribanexos[3] +"\n" );
						anexos_dados.setText( anexos_dados.getText().toString() + "Data: "+ atribanexos[4] );						
					}
				}
				//MOVIMENTACOES
				if(resultSet.getString(13).equals(""))
					movimentacoes_dados.setText("Nenhuma movimentação.");
				else
				{
					String[] objmovs = resultSet.getString(13).split("FIMREG");
					for( int x = 0; x < objmovs.length;x++ )
					{
						String[] atribmovs = objmovs[x].split("\\|");						
						if(x>0)
							movimentacoes_dados.setText( movimentacoes_dados.getText().toString() + "\n---\n" );
						movimentacoes_dados.setText( movimentacoes_dados.getText().toString() + "Caixa Destino: "+ atribmovs[1] +"\n" );
						movimentacoes_dados.setText( movimentacoes_dados.getText().toString() + "Caixa Origem: "+ atribmovs[0] +"\n" );
						movimentacoes_dados.setText( movimentacoes_dados.getText().toString() + "Usuário: "+ atribmovs[2] +"\n" );
						movimentacoes_dados.setText( movimentacoes_dados.getText().toString() + "Data: "+ atribmovs[3] );						
					}
				}
				//PENDENCIAS
				if(resultSet.getString(12).equals(""))
					pendencias_dados.setText("Nenhuma pendência.");
				else
				{
					String[] objpen = resultSet.getString(12).split("FIMREG");
					for( int x = 0; x < objpen.length;x++ )
					{
						String[] atribpen = objpen[x].split("\\|");						
						if(x>0)
							pendencias_dados.setText( pendencias_dados.getText().toString() + "\n---\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Tipo: "+ atribpen[2] +"\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Descrição: "+ atribpen[0] +"\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Parecer: "+ atribpen[1] +"\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Status: "+ atribpen[3] +"\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Usuário: "+ atribpen[4] +"\n" );
						pendencias_dados.setText( pendencias_dados.getText().toString() + "Data: "+ atribpen[5] );						
					}
				}
				//TITULO
				if(resultSet.getString(16).equals(""))
					titulo_dados.setText("Não titulado.");
				else
				{
					String[] objtit = resultSet.getString(16).split("\\|");
					titulo_dados.setText( titulo_dados.getText().toString() + "Número: " + objtit[0] +"\n" );
					titulo_dados.setText( titulo_dados.getText().toString() + "Tipo: " + objtit[1] +"\n" );
					titulo_dados.setText( titulo_dados.getText().toString() + "Status: " + objtit[2] );					
				}
			}
			catch(Exception e){
				Toast.makeText(this, "erro: "+e.getMessage()+". Informe ao Desenvolvedor.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
		else
		{
			Toast.makeText(this, "Não carregou / acessou o arquivo dos dados", Toast.LENGTH_LONG).show();
			finish();
		}

	}

	private void init()
	{
		tipo = (TextView) findViewById(R.id.tv_tipo);
		nome = (TextView) findViewById(R.id.tv_nome);
		localizacao = (TextView) findViewById(R.id.tv_localizacao);
		numero = (TextView) findViewById(R.id.tv_numero);
		cadastro_pessoa = (TextView) findViewById(R.id.tv_cadastro_pessoa);
		gleba = (TextView) findViewById(R.id.tv_gleba);
		municipio = (TextView) findViewById(R.id.tv_municipio);
		endereco = (TextView) findViewById(R.id.tv_endereco);
		contato = (TextView) findViewById(R.id.tv_contato);
		subnome = (TextView) findViewById(R.id.tv_subnome);
		classificacao = (TextView) findViewById(R.id.tv_anexo);
		pecas = (TextView) findViewById(R.id.tv_pecas);
		pecas_dados = (TextView) findViewById(R.id.tv_pecas_dados);
		pendencias = (TextView) findViewById(R.id.tv_pendencias);
		pendencias_dados = (TextView) findViewById(R.id.tv_pendencias_dados);
		movimentacoes = (TextView) findViewById(R.id.tv_movimentacoes);
		movimentacoes_dados = (TextView) findViewById(R.id.tv_movimentacoes_dados);
		anexos = (TextView) findViewById(R.id.tv_anexos);
		anexos_dados = (TextView) findViewById(R.id.tv_anexos_dados);
		titulo = (TextView) findViewById(R.id.tv_titulo);
		titulo_dados = (TextView) findViewById(R.id.tv_titulo_dados);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void click_bt_bar_back(View v) {
		// TODO Auto-generated method stub
		finish();
	}


	@Override
	public void abrirDialogProcessamento() {
		// TODO Auto-generated method stub
		pd = new ProgressDialog(this);
		pd.setTitle("");
		pd.setMessage(getString(R.string.ale_buscando_dados));
		pd.setCancelable(true);
		pd.setIndeterminate(true);
		pd.show();		
	}

	@Override
	public void fecharDialogProcessamento() {
		// TODO Auto-generated method stub
		if(pd != null)
			if(pd.isShowing())
				pd.dismiss();		
	}

	@Override
	public void finish() 
	{
		setResult(1, getIntent());
		super.finish();
	}

}
