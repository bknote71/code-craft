// Learn more about this file at:
// https://victorzhou.com/blog/build-an-io-game-part-1/#2-builds--project-setup
const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { EnvironmentPlugin } = require('webpack');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');

module.exports = {
  entry: {
    game: './src/main/resources/static/src/client/index.js',
    ingame: './src/main/resources/static/src/client/ingame.js',
    lobby: './src/main/resources/static/src/client/lobby.js',
    signup: './src/main/resources/static/src/client/signup.js',
  },
  output: {
    filename: '[name].[contenthash].js',
    path: path.resolve(__dirname, 'dist'),
  },
  externals: {
    'pixi.js': 'PIXI',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: ['@babel/preset-env'],
            plugins: ['@babel/plugin-proposal-optional-chaining']
          },
        },
      },
      {
        test: /\.css$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader,
          },
          'css-loader',
        ],
      },
      {
        test: /\.json$/,
        exclude: /node_modules/,
        loader: 'json-loader',
      },
    ],
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].[contenthash].css',
    }),
    new HtmlWebpackPlugin({
      chunks: ['game'],
      template: 'src/main/resources/static/src/client/html/index.html',
      filename: 'index.html',
    }),
    new HtmlWebpackPlugin({
      chunks: ['ingame'],
      template: 'src/main/resources/static/src/client/html/ingame.html',
      filename: 'ingame.html',
    }),
    new HtmlWebpackPlugin({
      chunks: ['lobby'],
      template: 'src/main/resources/static/src/client/html/lobby.html',
      filename: 'lobby.html',
    }),
    new HtmlWebpackPlugin({
      chunks: ['signup'],
      template: 'src/main/resources/static/src/client/html/signup.html',
      filename: 'signup.html',
    }),
  ],
};